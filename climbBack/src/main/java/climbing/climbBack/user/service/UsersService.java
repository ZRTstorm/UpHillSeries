package climbing.climbBack.user.service;

import climbing.climbBack.user.domain.Users;
import climbing.climbBack.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    // Firebase UID 를 받아서 Users 엔티티 반환 서비스
    @Transactional(readOnly = true)
    public Optional<Users> getUserByUid(String uid) {
        return usersRepository.findByUid(uid);
    }

    // 새로운 사용자 데이터 저장 서비스
    public Users saveUsers(Users users) {
        return usersRepository.save(users);
    }
}
