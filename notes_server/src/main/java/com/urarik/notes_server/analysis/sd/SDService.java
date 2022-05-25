package com.urarik.notes_server.analysis.sd;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SDService {
    private final SDRepository sdRepository;

    public SDService(SDRepositoryImpl sdRepositoryImpl) {
        this.sdRepository = sdRepositoryImpl;
    }

    public List<Map<String, Object>> getInvokes(Long pid, Long mid) {
        return sdRepository.getInvokes(pid, mid);
    }

    public List<Map<String, Object>> getBefore(Long pid, Long mid) {
        return sdRepository.getBefore(pid, mid);
    }
}
