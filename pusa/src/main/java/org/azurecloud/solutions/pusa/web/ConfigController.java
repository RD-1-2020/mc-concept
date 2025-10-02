package org.azurecloud.solutions.pusa.web;

import org.azurecloud.solutions.pusa.config.PasswordPolicyProperties;
import org.azurecloud.solutions.pusa.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/policy")
public class ConfigController {

    private final ConfigService configService;
    private final PasswordPolicyProperties passwordPolicyProperties;

    @Autowired
    public ConfigController(ConfigService configService, PasswordPolicyProperties passwordPolicyProperties) {
        this.configService = configService;
        this.passwordPolicyProperties = passwordPolicyProperties;
    }

    @GetMapping
    public String getPolicyPage(Model model) {
        model.addAttribute("policy", passwordPolicyProperties);
        return "policy";
    }

    @PostMapping("/update")
    public String updatePolicy(PasswordPolicyProperties policy) {
        configService.updateConfiguration("password.policy.min-length", String.valueOf(policy.getMinLength()));
        configService.updateConfiguration("password.policy.require-uppercase", String.valueOf(policy.isRequireUppercase()));
        configService.updateConfiguration("password.policy.require-lowercase", String.valueOf(policy.isRequireLowercase()));
        configService.updateConfiguration("password.policy.require-numbers", String.valueOf(policy.isRequireNumbers()));
        configService.updateConfiguration("password.policy.require-special", String.valueOf(policy.isRequireSpecial()));
        // Note: After this, a refresh event should be received via NATS to update the properties bean.
        // For immediate feedback on the UI, we might need to wait or manually trigger a refresh.
        return "redirect:/policy";
    }
}
