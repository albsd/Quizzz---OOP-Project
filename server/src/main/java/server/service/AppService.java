package server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import server.repository.AppRepository;

@Service
public class AppService {
    
    private final AppRepository appRepo;

    @Autowired
    public AppService(final AppRepository appRepo) {
        this.appRepo = appRepo;
    }

    public void saveNickname(final String macAddress, final String nick) {
        appRepo.saveNickname(macAddress, nick);
    }

    public String getNickname(final String macAddress) {
        return appRepo.getNickname(macAddress);
    }
}
