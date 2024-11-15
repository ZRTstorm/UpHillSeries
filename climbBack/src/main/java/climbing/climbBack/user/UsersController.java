package climbing.climbBack.user;

import climbing.climbBack.user.config.TokenRequest;
import climbing.climbBack.user.domain.UserIdResponse;
import climbing.climbBack.user.domain.UserRole;
import climbing.climbBack.user.domain.Users;
import climbing.climbBack.user.service.UsersService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UsersController {

    private final UsersService usersService;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody TokenRequest tokenRequest) {

        // idToken 추출
        String idToken = tokenRequest.getIdToken();

        try {
            // ID 토큰 검증
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            // UID 추출
            String uid = decodedToken.getUid();

            // 사용자 이메일 추출
            String email = decodedToken.getEmail();

            // DB 에서 사용자 조회
            Optional<Users> userOpt = usersService.getUserByUid(uid);

            // DB 에 사용자 정보가 없다면 새로 생성
            if (userOpt.isEmpty()) {
                Users newUser = new Users();
                newUser.setUid(uid);
                newUser.setEmail(email);
                newUser.setRole(UserRole.USER);

                Users users = usersService.saveUsers(newUser);
                Long userId = users.getId();

                // userId 응답
                UserIdResponse response = new UserIdResponse();
                response.setUserId(userId);

                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

            UserIdResponse response = new UserIdResponse();
            response.setUserId(userOpt.get().getId());

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            log.info("UserIdentification Error : {}", e.getMessage());
            return ResponseEntity.status(401).body("Invalid ID Token");
        }
    }

    @MessageMapping("/send")
    @SendTo("/queue/notification/1")
    public String receiveMessage(String message) {
        log.info("Message into Server = {}", message);
        return "From Server : " + message;
    }
}
