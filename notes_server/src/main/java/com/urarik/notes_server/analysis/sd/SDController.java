package com.urarik.notes_server.analysis.sd;

import com.urarik.notes_server.analysis.dto.PlaneWithName;
import com.urarik.notes_server.analysis.sd.table.SDPlane;
import com.urarik.notes_server.security.UserInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analyze/sd")
public class SDController {
    private final SDService sdService;
    private final UserInfo userInfo;

    public SDController(SDService sdService, UserInfo userInfo) {
        this.sdService = sdService;
        this.userInfo = userInfo;
    }

    @GetMapping("/invokes")
    public ResponseEntity<List<Map<String, Object>>> getInvokes(
            @RequestParam Long pid,
            @RequestParam Long mid
    ) {
        List<Map<String, Object>> res = sdService.getInvokes(pid, mid);

        return ResponseEntity.ok(res);
    }

    @GetMapping(value = "/before")
    public ResponseEntity<List<Map<String, Object>>> getBefore(
            @RequestParam Long pid,
            @RequestParam Long mid
    ) {
        List<Map<String, Object>> res = sdService.getBefore(pid, mid);

        return ResponseEntity.ok(res);
    }

    @GetMapping()
    public ResponseEntity<SDPlane> getSD(
            @RequestParam Long pid,
            @RequestParam Long planeId) {
        SDPlane sdPlane = sdService.getSD(pid, planeId, userInfo.getUsername());

        return ResponseEntity.ok(sdPlane);
    }

    @PostMapping(value = "/save")
    public ResponseEntity<Object> createSD(@RequestBody SDPlane sdPlane) {
        sdService.createPlane(sdPlane);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/saves")
    public ResponseEntity<List<PlaneWithName>> getPlaneList(
            @RequestParam Long pid
    ) {
        List<PlaneWithName> planeList = sdService.getPlaneList(pid, userInfo.getUsername());

        return ResponseEntity.ok(planeList);
    }
}
