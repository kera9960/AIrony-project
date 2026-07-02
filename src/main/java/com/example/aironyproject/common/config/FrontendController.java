package com.example.aironyproject.common.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping({
            "/",
            "/accommodations",
            "/search",
            "/detail",
            "/checkout",
            "/complete",
            "/coupons",
            "/my-coupons",
            "/likes",
            "/reservations",
            "/reservation-detail",
            "/my-inquiries",
            "/inquiries",
            "/admin",
            "/auth",
            "/my"
    })
    public String forwardFrontend() {
        return "forward:/index.html";
    }
}
