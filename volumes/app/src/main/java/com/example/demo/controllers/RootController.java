package com.example.demo.controllers;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.example.demo.models.InquiryForm;
import com.example.demo.repositries.InquiryRepository;
import ch.qos.logback.core.joran.spi.EventPlayer;
import java.util.List;
import javax.transaction.Transactional;
import com.example.demo.controllers.RedirectUrlProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
@Transactional
@RequiredArgsConstructor
public class RootController {
//    private final RedirectUrlProperties redirectUrlProperties;

	@Autowired
	RedirectUrlProperties redirectUrlProperties;
	
    @Autowired
    InquiryRepository repository;

    public String buildAdminRedirectUrl(String url) {
        return UriComponentsBuilder.fromUriString(redirectUrlProperties.getUrl() + url).toUriString();
    }

    @GetMapping
    public String index() {
        return "root/index";
    }

    @GetMapping("/list")
    public String inquiryList(Model model) {
        List<InquiryForm> items = repository.findAll();
        model.addAttribute("items", items);
        return "root/list";
    }

    @GetMapping("/form")
    public String form(InquiryForm inquiryForm) {
        return "root/form";
    }

    @PostMapping("/form")
    public String form(@Validated InquiryForm inquiryForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "root/form";
        }
        repository.save(inquiryForm);
        model.addAttribute("message", "お問い合わせを受け付けました。");
        return "root/form";
    }

    @GetMapping("{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        InquiryForm inquiryform = repository.findById(id).get();
        model.addAttribute("inquiryform", inquiryform);
        return "root/edit";
    }

    @PutMapping("{id}")
    public ModelAndView update(@PathVariable Long id, @ModelAttribute InquiryForm inquiryform) {
        inquiryform.setId(id);
        repository.save(inquiryform);
        return new ModelAndView("redirect:" + buildAdminRedirectUrl("/list"));
    }

    @DeleteMapping("{id}")
    public ModelAndView destroy(@PathVariable Long id) {
        repository.deleteById(id);
        return new ModelAndView("redirect:" + buildAdminRedirectUrl("/list"));
    }
}