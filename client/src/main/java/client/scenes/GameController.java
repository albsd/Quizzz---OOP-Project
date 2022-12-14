package client.scenes;

import client.FXMLController;
import client.sounds.Sound;
import client.sounds.SoundName;
import client.utils.ServerUtils;
import client.utils.WebSocketSubscription;

import commons.QuestionTimer;
import commons.Player;
import commons.Game;
import commons.EmoteMessage;
import commons.GameUpdate;
import commons.Leaderboard;
import commons.Question;
import commons.NumberMultipleChoiceQuestion;
import commons.ActivityMultipleChoiceQuestion;
import commons.FreeResponseQuestion;
import commons.MultipleChoiceQuestion;
import commons.Emote;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimerTask;
import java.util.Date;
import java.util.Collections;
public class GameController implements Initializable, WebSocketSubscription {

    @FXML
    private Button option1, option2, option3,
            activityOption1, activityOption2, activityOption3, timeButton;
    
    @FXML
    private Label questionPrompt, questionNumber, points,
            warning, answerBox, questionPoint, correctText, incorrectText;


    @FXML
    private ProgressBar progressBar;

    @FXML
    private ScrollPane emoteScroll;

    @FXML
    private AnchorPane overlapAnchor;

    @FXML
    private VBox emoteChat, optionBox, multipleVBox1, multipleVBox2, multipleVBox3;

    @FXML
    private HBox singleImage, tripleImage;

    @FXML
    private AnchorPane menu;

    @FXML
    private HBox mainHorizontalBox;

    @FXML
    private ImageView questionImage, questionImage1, questionImage2, questionImage3;

    @FXML
    private Region imageRegion1, imageRegion2, imageRegion3, imageOptionRegion,
            multipleImageRegion1, multipleImageRegion2;

    @FXML
    private TextField openAnswer;

    @FXML
    private SVGPath soundIcon;

    @FXML
    private PopupController popupController;

    @FXML
    private LeaderboardController leaderboardController;

    private final FXMLController fxml;

    private final ServerUtils server;

    private final QuestionTimer clientTimer;

    private Player me;

    private Game game;

    private String chatPath;

    private boolean isOpenQuestion = false, muted, isSinglePlayer;
    
    private String leavePath;

    private boolean doubleScore;

    private boolean submittedAnswer;

    private int currentScore, answeredCorrectly;

    private double multiImageSize;

    private List<Button> optionButtons;
    private List<Button> activityOptionButtons;
    
    private Date then;

    private final long kTHRESHHOLD = 200; // ms

    private boolean showingAnswer = false;

    @Inject
    public GameController(final ServerUtils server, final FXMLController fxml) {
        this.clientTimer = initClientTimer();
        this.server = server;
        this.fxml = fxml;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        this.then = new Date();
        multiImageSize = 239;
        optionButtons = new ArrayList<>(Arrays.asList(option1, option2, option3));
        optionButtons.forEach((b) -> {
            b.setWrapText(true);
            b.setPrefWidth(145);
        });

        activityOptionButtons = new ArrayList<>(Arrays.asList(activityOption1, activityOption2, activityOption3));
        activityOptionButtons.forEach((b) -> {
            b.setWrapText(true);
        });

        submittedAnswer = false;
        doubleScore = false;
        answeredCorrectly = 0;
        currentScore = 0;
        openAnswer.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null,
                change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?([1-9][0-9]*)?")) {
                return change;
            }
            return null;
        }));

        muted = false;
        isSinglePlayer = false;
    }

    /**
     * On each tick, update the progressBar's value and calculate the color.
     * The color animates from green - orange - red.
     * An animated color is interpolated based on the remaining time.
     * x [1, 0.5] -> y [1, 0]; x [0.5, 0] -> y [1, 0]
     *
     * @return an instance of the QuestionTimer for the client
     */
    private QuestionTimer initClientTimer() {
        Color green = Color.valueOf("#e0fccf");
        Color orange = Color.valueOf("#ffd029");
        Color red = Color.valueOf("fe6f5b");

        return new QuestionTimer(
                time -> Platform.runLater(() -> {
                    double ratio = (double) time / QuestionTimer.MAX_TIME;
                    progressBar.setProgress(ratio);

                    Color rgb; // linearly interpolate
                    double y = 1 - Math.abs(2 * ratio - 1);
                    if (ratio > 0.5) {
                        rgb = orange.interpolate(green, 1 - y);
                    } else {
                        rgb = red.interpolate(orange, y);
                    }
                    progressBar.setStyle("-fx-accent: #" + rgb.toString().substring(2) + ";");
                }), this::sendFinishMessage);
    }

    /**
     * Registers the player to the server's messages
     * for chat messages from other players
     * and disconnection updates in the chat
     * and time halving updates.
     * @return the subscriptions of the client
     */
    @Override
    public Subscription[] registerForMessages() {
        Subscription[] subscriptions = new Subscription[3];
        subscriptions[0] = server.registerForMessages("/topic" + chatPath, EmoteMessage.class, message -> {
            Platform.runLater(() -> {
                String emotePath = switch (message.getContent()) {
                    case cry -> "/images/svgs/face-sad.svg";
                    case frown -> "/images/svgs/face-frown.svg";
                    case smile -> "/images/svgs/face-smile.svg";
                    case surprised -> "/images/svgs/face-surprise.svg";
                    case reducedTime -> "/images/svgs/time.svg";
                };
                
                updateEmoteBox(message.getNick(), emotePath);
            });
        });

        subscriptions[1] = server.registerForMessages("/topic" + leavePath, Player.class, player -> {
            Platform.runLater(() -> {
                updateEmoteBox(player.getNick(), "/images/svgs/leave.svg");
            });
        });

        subscriptions[2] = server.registerForMessages("/topic/game/" + game.getId()
                + "/update", GameUpdate.class, update -> {
            Platform.runLater(() -> {
                switch (update) {
                    case halveTimer -> {
                        Sound boonSound = new Sound(SoundName.boon);
                        boonSound.play(muted, false);

                        clientTimer.halve();
                    }
                    case startTimer -> {
                        displayAnswerMomentarily();
                    }
                    case stopTimer -> clientTimer.stop();
                    default -> { }
                }
            });
        });

        return subscriptions;
    }

    /**
    * Updates the emote box with the given event.
     * 
     * @param nick Player who sends an event
     * @param svgFilePath File path to SVG to be displayed as result of the event
     */
    private void updateEmoteBox(final String nick, final String svgFilePath) {
        Label nickname = new Label(nick);

        SVGPath svg = new SVGPath();
        svg.setContent(fxml.loadSVGPath(svgFilePath));
        svg.setScaleX(0.125);
        svg.setScaleY(0.125);
        svg.setTranslateX(-svg.getLayoutBounds().getCenterX() + 32);
        svg.setTranslateY(-svg.getLayoutBounds().getCenterY() + 32);

        Pane svgHolder = new Pane();
        svgHolder.setPrefSize(64, 64);
        svgHolder.setMinSize(64, 64);
        svgHolder.getChildren().add(svg);

        HBox emoteBox = new HBox(20);
        emoteBox.setAlignment(Pos.CENTER_RIGHT);
        emoteBox.getChildren().addAll(nickname, svgHolder);
        emoteChat.getChildren().add(emoteBox);
        emoteScroll.layout();
        emoteScroll.setVvalue(1);
    }

    /**
     * Assigns the currentGame and myself as a Player.
     * Initializes the game's timer and start the game loop.
     * 
     * @param me    Player of myself
     * @param game  Current game that I'm a part of
     */
    public void setGame(final Player me, final Game game) {
        this.me = me;
        this.game = game;
        this.chatPath = "/game/" + game.getId() + "/chat";
        this.leavePath = "/game/" + game.getId() + "/leave";

        displayCurrentQuestion();
        clientTimer.start(0);
        server.startHeartbeat(new TimerTask() {
            @Override
            public void run() {
                me.updateTimestamp(new Date());
                server.updateGamePlayer(game.getId(), me.getNick());
            }
        });
    }

    public void setSinglePlayer(final Game game) {
        this.me = game.getPlayers().get(0); // only 1 player
        this.game = game;

        //Removing power-ups
        mainHorizontalBox.getChildren().remove(0);
        //Removing region
        mainHorizontalBox.getChildren().remove(0);
        //Removing chat
        mainHorizontalBox.getChildren().remove(1);
        //Removing region
        mainHorizontalBox.getChildren().remove(1);
        overlapAnchor.setPrefWidth(1240);
        singleImage.setPrefWidth(1210);
        tripleImage.setPrefWidth(1210);
        optionBox.setPrefWidth(500);
        imageOptionRegion.setPrefWidth(250);
        multipleImageRegion1.setPrefWidth(173);
        multipleImageRegion2.setPrefWidth(173);
        multipleVBox1.setPrefWidth(288);
        multipleVBox2.setPrefWidth(288);
        multipleVBox3.setPrefWidth(288);
        questionImage1.setFitWidth(288);
        questionImage2.setFitWidth(288);
        questionImage3.setFitWidth(288);
        questionImage1.setFitHeight(288);
        questionImage2.setFitHeight(288);
        questionImage3.setFitHeight(288);

        optionBox.setAlignment(Pos.CENTER);
        optionBox.setPrefWidth(600);
        optionBox.setPadding(Insets.EMPTY);
        VBox.setMargin(optionBox, new Insets(75, 0, 0, 0));

        optionButtons.forEach((b) -> b.setPrefHeight(145));
        multiImageSize = 288;

        displayCurrentQuestion();
        clientTimer.start(0);

        isSinglePlayer = true;
    }

    /**
     * Checks the answer for a multiple-choice type of question
     * and updates the style of the chosen button.
     *
     * @param e triggered by a button click
     */
    @FXML
    public void checkMulChoiceOption(final ActionEvent e) {
        if (validateAnswerSubmission()) {
            submittedAnswer = true;
            Button chosenOption = (Button) e.getSource();
            Button[] options;

            chosenOption.getStyleClass().add("selectedOption");
            if (game.getCurrentQuestion() instanceof ActivityMultipleChoiceQuestion) {
                options = new Button[] {activityOption1, activityOption2, activityOption3};
            } else {
                options = new Button[] {option1, option2, option3};
            }
            long option = ArrayUtils.indexOf(options, chosenOption);
            checkAnswer(option, clientTimer.getCurrentTime());
        }
        if (!game.isMultiplayer()) {
            clientTimer.setCurrentTime(0);
        }
    }

    /**
     * Updates the text in the warning label based on the state of the client's timer
     * and based on whether they already submitted an answer.
     *
     * @return true if the answer submission is valid, false otherwise
     */
    public boolean validateAnswerSubmission() {
        if (submittedAnswer) {
            warning.setText("Already submitted answer!");
            warning.setVisible(true);
            return false;
        }
        if (clientTimer.isOver()) {
            warning.setText("Too late! Time's over!");
            warning.setVisible(true);
            return false;
        }
        return true;
    }

    /**
     * Displays and validates the answer for open question.
     *
     * @param event triggered by a button click
     */
    @FXML
    public void onEnter(final ActionEvent event) {
        String optionStr = openAnswer.getText();
        openAnswer.setDisable(true);
        if (validateAnswerSubmission()) {
            submittedAnswer = true;
            long option = Long.parseLong(optionStr);
            checkAnswer(option, clientTimer.getCurrentTime());
        }
        if (!game.isMultiplayer()) {
            clientTimer.setCurrentTime(0);
        }
    }

    /**
     * Validates and displays the answer for the multiple choice question.
     *
     * @param option the chosen answer to the question
     * @param time the time left in ms
     */
    public void checkAnswer(final long option, final int time) {
        Sound optionSound = new Sound(SoundName.pop);
        optionSound.play(muted || isSinglePlayer, false);
        currentScore = game.getCurrentQuestion().calculateScore(option, time);
        if (currentScore > 0) answeredCorrectly++;
    }

    /**
     * Displays the answer to a question in-between rounds.
     * (for five seconds)
     */
    private void displayAnswerMomentarily() {
        if (game.getCurrentQuestionNumber() > 20) return;
        showingAnswer = true;
        if (doubleScore) {
            currentScore *= 2;
            doubleScore = false;
        }
        me.addScore(currentScore);
        server.addScore(game.getId(), me.getNick(), currentScore);

        Platform.runLater(() -> {
            progressBar.setProgress(0.0);
            points.setText("Total points: " + me.getScore());
            correctText.setText(String.valueOf(answeredCorrectly));
            incorrectText.setText(String.valueOf(game.getCurrentQuestionNumber() - answeredCorrectly));
            questionPoint.setText("You got: " + currentScore + " points");
            if (currentScore > 0) {
                Sound rightAnswerSound = new Sound(SoundName.right_answer);
                rightAnswerSound.play(muted, false);
            } else {
                Sound wrongAnswerSound = new Sound(SoundName.wrong_answer);
                wrongAnswerSound.play(muted, false);
            }
            currentScore = 0;
        });
        warning.setVisible(false);
        long answer = game.getCurrentQuestion().getAnswer();
        if (!isOpenQuestion) {
            Button[] options;
            if (game.getCurrentQuestion() instanceof ActivityMultipleChoiceQuestion) {
                options = new Button[] {activityOption1, activityOption2, activityOption3};
            } else {
                options = new Button[] {option1, option2, option3};
            }
            for (int i = 0; i < options.length; i++) {
                if (i == answer) {
                    options[i].getStyleClass().add("correctAnswer");
                } else {
                    options[i].getStyleClass().add("wrongAnswer");
                }
            }
        } else {
            Platform.runLater(() -> answerBox.setText("Answer is: " + answer));
        }
        clientTimer.startDelay(this::setNextQuestion);
    }

    /**
     * Sets the next question, in case we are past the 10th question
     * sends the player score and displays the leaderboard above the current screen.
     */
    public void setNextQuestion() {
        // Displays leaderboard at end of game
        if (game.isOver()) {
            if (game.isMultiplayer()) {
                server.cancelHeartbeat();
                displayLeaderboardMomentarily(server.getLeaderboard(game.getId()));
                server.setGameOver(game.getId());
            } else {
                server.sendGameResult(this.me.getNick(), this.me.getScore());
                displayLeaderboardMomentarily(server.getSinglePlayerLeaderboard());
            }
        }
        int delay = 0;
        // Displays leaderboard every 10 questions in multiplayer
        if (game.isMultiplayer() && game.shouldShowMultiplayerLeaderboard()) {
            delay = 5000;
            displayLeaderboardMomentarily(server.getLeaderboard(game.getId()));

        }
        if (!game.isOver()) {
            Platform.runLater(() -> {
                openAnswer.setText("");
                answerBox.setText("");
            });
            submittedAnswer = false;
            warning.setVisible(false);
            game.nextQuestion();
            displayCurrentQuestion();
            clientTimer.start(delay);
            showingAnswer = false;
            openAnswer.setDisable(false);
        }
    }


    /**
     * Displays leaderboard for five seconds.
     *
     * @param leaderboard leaderboard to be displayed
     */
    private void displayLeaderboardMomentarily(final Leaderboard leaderboard) {
        Platform.runLater(() -> {
            leaderboardController.displayLeaderboard(leaderboard, me);
            menu.setVisible(false);
            if (!game.isOver()) {
                leaderboardController.hideBackButton();
            }
            leaderboardController.show();
        });

        KeyFrame kf = new KeyFrame(Duration.seconds(5), e -> {
            if (game.isOver()) {
                leaderboardController.endGame(me);
            }
            if (!game.isOver()) {
                //clientTimer.startDelay(this::sendFinishMessage);
                leaderboardController.hide();
                menu.setVisible(true);
            }
        });
        Timeline timeline = new Timeline(kf);
        Platform.runLater(timeline::play);
    }

    /**
     * Displays the current question and updates visual elements accordingly.
     */
    private void displayCurrentQuestion() {
        Platform.runLater(() -> {
            Question currentQuestion = game.getCurrentQuestion();
            if (currentQuestion instanceof NumberMultipleChoiceQuestion numQuestion) {
                changeToNumberMultiMode();
                isOpenQuestion = false;
                Image img = new Image(new ByteArrayInputStream(numQuestion.getImage()), 340, 340, false, true);
                questionImage.setImage(img);
            } else if (currentQuestion instanceof FreeResponseQuestion openQuestion) {
                changeToFreeMode();
                isOpenQuestion = true;
                Image img = new Image(new ByteArrayInputStream(openQuestion.getImage()), 340, 340, false, true);
                questionImage.setImage(img);
            } else {
                ActivityMultipleChoiceQuestion activityQuestion = (ActivityMultipleChoiceQuestion) currentQuestion;
                isOpenQuestion = false;
                Image img1 = new Image(new ByteArrayInputStream(activityQuestion.getImages()[0]),
                        multiImageSize, multiImageSize, false, true);
                Image img2 = new Image(new ByteArrayInputStream(activityQuestion.getImages()[1]),
                        multiImageSize, multiImageSize, false, true);
                Image img3 = new Image(new ByteArrayInputStream(activityQuestion.getImages()[2]),
                        multiImageSize, multiImageSize, false, true);
                changeToActivityMultiMode(img1, img2, img3);
            }

            questionNumber.setText(String.format("%d/%d", game.getCurrentQuestionNumber(), 20));
            questionPrompt.setText(currentQuestion.getPrompt());
            questionPoint.setText("");
            if (currentQuestion instanceof MultipleChoiceQuestion) {
                String[] options = ((MultipleChoiceQuestion) currentQuestion).getOptions();

                optionButtons.forEach((b) -> {
                    b.setDisable(false);
                    b.getStyleClass().removeAll("wrongAnswer", "correctAnswer", "selectedOption", "removedOption");
                    b.setText(options[optionButtons.indexOf(b)]);
                });

                activityOptionButtons.forEach((b) -> {
                    b.setDisable(false);
                    b.getStyleClass().removeAll("wrongAnswer", "correctAnswer", "selectedOption", "removedOption");
                    b.setText(options[activityOptionButtons.indexOf(b)]);
                });
            }
        });
    }

    public void sendFinishMessage() {
        if (game.isMultiplayer()) {
            server.updatePlayerFinished(game.getId(), me.getNick());
        } else {
            displayAnswerMomentarily();
        }
    }

    @FXML
    public void openPopup(final ActionEvent e) {
        new Sound(SoundName.click).play(false, false);
        popupController.open("Do you really want to leave the game?", () -> {
            if (game.isMultiplayer()) {
                server.updatePlayerFinished(game.getId(), me.getNick());
                server.cancelHeartbeat();
                server.leaveGame(me.getNick(), game.getId());
            }
            clientTimer.stop();
            //mark game over to prevent next callback
            game.setCurrentQuestionIndex(20);
            fxml.showSplash();
        });
    }

    /**
     * Cuts every player's remaining time in half except that of who clicked the button.
     * @param e triggered by a button click
     */
    @FXML
    public void timePowerup(final ActionEvent e) {
        if (showingAnswer) {
            warning.setText("Power-up not available!");
            warning.setVisible(true);
        } else {
            new Sound(SoundName.click).play(false, false);
            server.send("/app/game/" + this.game.getId() + "/halve", GameUpdate.halveTimer);
            server.send("/app" + chatPath, new EmoteMessage(me.getNick(), Emote.reducedTime));
            // Solution to ensure that the initiator's timer is not halved
            clientTimer.setCurrentTime(clientTimer.getCurrentTime() * 2);
            timeButton.setDisable(true);
        }
    }

    /**
     * For a round, doubles the points of the player.
     * @param e triggered by a button click
     */
    @FXML
    public void scorePowerup(final ActionEvent e) {
        if (showingAnswer) {
            warning.setText("Power-up not available!");
            warning.setVisible(true);
        } else {
            new Sound(SoundName.click).play(false, false);
            doubleScore = true;
            ((Button) e.getSource()).setDisable(true);
        }
    }

    /**
     * For a round, removes an incorrect answer for the player.
     * @param e triggered by a button click
     */
    @FXML
    public void removePowerup(final ActionEvent e) {
        if (!isOpenQuestion && validateAnswerSubmission()) {
            new Sound(SoundName.click).play(false, false);

            ((Button) e.getSource()).setDisable(true);
            Button[] options;
            if (game.getCurrentQuestion() instanceof ActivityMultipleChoiceQuestion) {
                options = new Button[] {activityOption1, activityOption2, activityOption3};
            } else {
                options = new Button[] {option1, option2, option3};
            }
            List<Integer> removeIndices = new ArrayList<>(Arrays.asList(0, 1, 2));
            int answerIndex = (int) game.getCurrentQuestion().getAnswer();
            removeIndices.remove(Integer.valueOf(answerIndex));
            Collections.shuffle(removeIndices);

            int finalIndex = removeIndices.get(0);
            options[finalIndex].setDisable(true);
            options[finalIndex].getStyleClass().add("removedOption");
        } else {
            warning.setText("Power-up not available!");
            warning.setVisible(true);
        }
    }

    @FXML
    public void frown(final ActionEvent e) {
        sendEmote(Emote.frown);
    }

    @FXML
    public void cry(final ActionEvent e) {
        sendEmote(Emote.cry);
    }

    @FXML
    public void smile(final ActionEvent e) {
        sendEmote(Emote.smile);
    }

    @FXML
    public void surprised(final ActionEvent e) {
        sendEmote(Emote.surprised);
    }

    private void sendEmote(final Emote emote) {
        Date now = new Date();
        if (now.getTime() - then.getTime() < kTHRESHHOLD) return;
        then = now;
        
        new Sound(SoundName.click).play(false, false);
        
        server.send("/app" + chatPath, new EmoteMessage(me.getNick(), emote));
    }

    /**
     * Updates the interface to reflect a multiple-choice question.
     */
    private void changeToNumberMultiMode() {
        tripleImage.setVisible(false);
        singleImage.setVisible(true);
        answerBox.toFront();
        openAnswer.toFront();
        openAnswer.setVisible(false);
        optionButtons.forEach((b) -> b.setVisible(true));
    }

    /**
     * Updates the interface to reflect an open-choice question.
     */
    private void changeToFreeMode() {
        tripleImage.setVisible(false);
        singleImage.setVisible(true);
        answerBox.toBack();
        openAnswer.toBack();
        openAnswer.setVisible(true);
        optionButtons.forEach((b) -> b.setVisible(false));
    }

    private void changeToActivityMultiMode(final Image img1, final Image img2, final Image img3) {
        singleImage.setVisible(false);
        tripleImage.setVisible(true);
        double height1 = img1.getHeight();
        double height2 = img2.getHeight();
        double height3 = img3.getHeight();
        imageRegion1.setPrefHeight(multiImageSize - height1 + 25);
        imageRegion2.setPrefHeight(multiImageSize - height2 + 25);
        imageRegion3.setPrefHeight(multiImageSize - height3 + 25);
        questionImage1.setImage(img1);
        questionImage2.setImage(img2);
        questionImage3.setImage(img3);
    }

    @FXML
    private void updateSoundButton(final ActionEvent e) {
        new Sound(SoundName.click).play(false, false);
        if (muted) {
            muted = false;
            soundIcon.setContent(fxml.loadSVGPath("/images/svgs/sound.svg"));
        } else {
            muted = true;
            soundIcon.setContent(fxml.loadSVGPath("/images/svgs/mute.svg"));
        }
    }
}
