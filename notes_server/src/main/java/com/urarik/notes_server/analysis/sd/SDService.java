package com.urarik.notes_server.analysis.sd;

import com.urarik.notes_server.analysis.dto.PlaneWithName;
import com.urarik.notes_server.analysis.sd.table.SDPlane;
import com.urarik.notes_server.project.ProjectService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SDService {
    private final SDRepository sdRepository;
    private final SDPlaneRepository sdPlaneRepository;
    private final ProjectService projectService;

    public SDService(SDRepositoryImpl sdRepositoryImpl, SDPlaneRepository sdPlaneRepository, ProjectService projectService) {
        this.sdRepository = sdRepositoryImpl;
        this.sdPlaneRepository = sdPlaneRepository;
        this.projectService = projectService;
    }

    public List<Map<String, Object>> getInvokes(Long pid, Long mid) {
        return sdRepository.getInvokes(pid, mid);
    }

    public List<Map<String, Object>> getBefore(Long pid, Long mid) {
        return sdRepository.getBefore(pid, mid);
    }

    public void createPlane(SDPlane sdPlane) {
        sdPlaneRepository.save(sdPlane);
    }

    public List<PlaneWithName> getPlaneList(Long pid, String userName) {
        if(!projectService.isMember(pid, userName)) return null;

        return sdPlaneRepository.findPlanesByPid(pid);
    }

    public SDPlane getSD(Long pid, Long planeId, String userName) {
        if(!projectService.isMember(pid, userName)) return null;

        return sdPlaneRepository.findById(planeId).orElse(null);
    }
}
