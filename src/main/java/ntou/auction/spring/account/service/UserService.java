package ntou.auction.spring.account.service;

import ntou.auction.spring.account.entity.User;

import java.util.Optional;
import java.util.Set;

import ntou.auction.spring.account.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> get(Long id) {
        return repository.findById(id);
    }

    public User update(User entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        Optional<User> maybeUser = repository.findById(id);
        if (maybeUser.isPresent()) {
            //User user = maybeUser.get();
            repository.deleteById(id);
        }
    }

    public Page<User> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<User> list(Pageable pageable, Specification<User> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    public void store(User user) {
        repository.save(user);
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public boolean isUsernameNonExist(String username) {
        return repository.findByUsername(username) == null;
    }

    public boolean isEmailNonExist(String email) {
        return repository.findAllByEmail(email).isEmpty();
    }

    public User findByUsername(String userName) {
        return repository.findByUsername(userName);
    }

    public Set<Long> getFavoriteProducts(Long userId) {
        if (repository.findById(userId).isPresent()) {
            return repository.findById(userId).get().getFavoriteProducts();
        } else {
            return null;
        }
    }

    public boolean addFavoriteProducts(Long userId, Long productId) {
        if (repository.findById(userId).isPresent()) {
            User user = repository.findById(userId).get();
            Set<Long> favoriteProducts = user.getFavoriteProducts();
            if (!favoriteProducts.add(productId)) {
                return false;
            }
            user.setFavoriteProducts(favoriteProducts);
            repository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeFavoriteProducts(Long userId, Long productId) {
        if (repository.findById(userId).isPresent()) {
            User user = repository.findById(userId).get();
            Set<Long> favoriteProducts = user.getFavoriteProducts();
            if (!favoriteProducts.remove(productId)) {
                return false;
            }
            user.setFavoriteProducts(favoriteProducts);
            repository.save(user);
            return true;
        } else {
            return false;
        }
    }
}
