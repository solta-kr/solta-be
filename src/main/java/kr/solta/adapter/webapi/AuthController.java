package kr.solta.adapter.webapi;

import static kr.solta.adapter.webapi.request.Client.EXTENSION;
import static kr.solta.adapter.webapi.request.Client.WEB;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.solta.adapter.github.GithubProperties;
import kr.solta.adapter.webapi.properties.ClientProperties;
import kr.solta.adapter.webapi.request.Client;
import kr.solta.adapter.webapi.response.OauthLoginUrlResponse;
import kr.solta.application.provided.AuthTokenCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthTokenCreator authTokenCreator;
    private final GithubProperties githubProperties;
    private final ClientProperties clientProperties;

    @GetMapping("/oauth/github/login")
    public ResponseEntity<OauthLoginUrlResponse> getGithubLoginUrl(@RequestParam final Client client) {
        String loginUrl = githubProperties.loginUrl() + "&state=" + client.name();

        return ResponseEntity.ok(new OauthLoginUrlResponse(loginUrl));
    }

    @GetMapping("/code/github")
    public void login(
            final HttpServletResponse response,
            @RequestParam final String code,
            @RequestParam("state") final Client client
    ) throws IOException {
        String accessToken = authTokenCreator.createAuthToken(code);

        if (client == EXTENSION) {
            String url = "https://" + clientProperties.extensionId() + ".chromiumapp.org?token=" + accessToken;
            response.sendRedirect(url);
            return;
        }

        if (client == WEB) {
            String url = clientProperties.webUrl() + "login/success?token=" + accessToken;
            response.sendRedirect(url);
        }
    }
}
