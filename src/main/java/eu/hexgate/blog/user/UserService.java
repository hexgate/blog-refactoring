package eu.hexgate.blog.user;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isVip(String userId) {
        return userRepository.findById(userId)
                .map(User::isVip)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
