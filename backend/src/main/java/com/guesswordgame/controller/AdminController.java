package com.guesswordgame.controller;

import com.guesswordgame.dto.GameDtos;
import com.guesswordgame.service.AdminService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/reports/day")
    public GameDtos.DailyReport dayReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return adminService.dayReport(date);
    }

    @GetMapping("/reports/user/{username}")
    public List<GameDtos.UserDailyReport> userReport(@PathVariable String username) {
        return adminService.userReport(username);
    }

    @GetMapping("/words")
    public List<GameDtos.WordResponse> words() {
        return adminService.words();
    }

    @PostMapping("/words")
    public GameDtos.WordResponse addWord(@RequestBody GameDtos.WordRequest request) {
        return adminService.addWord(request.value());
    }

    @DeleteMapping("/words/{id}")
    public void deleteWord(@PathVariable Long id) {
        adminService.deleteWord(id);
    }
}
