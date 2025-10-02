package org.azurecloud.solutions.pusa.web;

import org.azurecloud.solutions.pusa.service.ConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/config")
@RefreshScope
public class ConfigController {

    private final ConfigService configService;

    @Value("${user.password.min-length}")
    private int minPasswordLength;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    // HTML Page
    @GetMapping("/policy")
    public String policyPage(Model model) {
        model.addAttribute("minLength", minPasswordLength);
        return "policy";
    }

    @PostMapping("/policy")
    public String updatePolicy(@RequestParam("minLength") String minLength) {
        configService.updateConfiguration("user.password.min-length", minLength);
        return "redirect:/config/policy";
    }

    // REST API
    @GetMapping("/api/min-length")
    @ResponseBody
    public Map<String, Object> getMinLength() {
        return Map.of("user.password.min-length", minPasswordLength);
    }

    @PostMapping("/api/min-length")
    @ResponseBody
    public Map<String, String> updateMinLength(@RequestBody Map<String, String> payload) {
        String value = payload.get("value");
        configService.updateConfiguration("user.password.min-length", value);
        return Map.of("status", "ok");
    }
}
