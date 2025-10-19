package org.example.ainewsletter.application;

import lombok.NonNull;
import org.example.ainewsletter.core.use_cases.CreateNewsletter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class NewsController {

    private final CreateNewsletter createNewsletter;

    public NewsController(@NonNull final CreateNewsletter createNewsletter) {
        this.createNewsletter = createNewsletter;
    }


    @GetMapping(value = "/newsletter", produces = MediaType.TEXT_HTML_VALUE)
    public String getNews(@RequestParam String subject) {
        return createNewsletter.createForSubject(subject);
    }

}
