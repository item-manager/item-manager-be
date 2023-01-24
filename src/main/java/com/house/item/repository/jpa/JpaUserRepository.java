package com.house.item.repository.jpa;

import com.house.item.entity.User;
import com.house.item.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {

    private final EntityManager em;

    @Override
    public Long save(User user) {
        em.persist(user);
        return user.getUserNo();
    }

    @Override
    public Optional<User> findOne(Long userNo) {
        return Optional.ofNullable(em.find(User.class, userNo));
    }

    @Override
    public Optional<User> findById(String id) {
        String jpql = "select u from User u where u.id=:id";
        List<User> user = em.createQuery(jpql, User.class)
                .setParameter("id", id)
                .getResultList();
        return user.stream().findAny();
    }

    @Override
    public void delete(User user) {
        em.remove(user);
    }
}
