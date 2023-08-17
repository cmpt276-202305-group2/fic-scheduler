package com.group2.server.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class ReactForwardController {
    @Value("classpath:/public/asset-manifest.json")
    Resource assetManifestJsonFile;

    @Value("classpath:/public/index.html")
    Resource indexHtmlFile;

    @Value("classpath:/public/manifest.json")
    Resource manifestJsonFile;

    @GetMapping("/asset-manifest.json")
    @ResponseBody
    Resource assetManifestJson() {
        return assetManifestJsonFile;
    }

    @GetMapping("/manifest.json")
    @ResponseBody
    Resource defaultManifestJson() {
        return manifestJsonFile;
    }

    @GetMapping
    @ResponseBody
    Resource defaultIndexHtml() {
        return indexHtmlFile;
    }

    @GetMapping("/*")
    @ResponseBody
    Resource wildcardIndexHtml() {
        return indexHtmlFile;
    }

    @GetMapping("/debugMenu/*")
    @ResponseBody
    Resource wildcardDebugMenuIndexHtml() {
        return indexHtmlFile;
    }
}
