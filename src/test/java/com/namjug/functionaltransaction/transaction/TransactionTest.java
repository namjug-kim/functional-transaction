package com.namjug.functionaltransaction.transaction;

import com.namjug.functionaltransaction.entity.UserEntity;
import com.namjug.functionaltransaction.entity.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TransactionTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    public void save() {
        // GIVEN
        // save user id 1
        Transaction
                .create(() -> {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(1L);
                    userEntity.setUserName("테스트유저");
                    return userRepository.save(userEntity);
                })
                .execute(transactionManager, Propagation.REQUIRES_NEW);

        // save user id 2
        try {
            Transaction
                    .create(() -> {
                        UserEntity userEntity = new UserEntity();
                        userEntity.setId(2L);
                        userEntity.setUserName("테스트유저");
                        return userRepository.save(userEntity);
                    })
                    .map(userEntity -> {
                        // throw exception
                        throw new RuntimeException();
                    })
                    .execute(transactionManager, Propagation.REQUIRES_NEW);
        } catch (Exception ignore) {
        }

        // WHEN
        Optional<UserEntity> id1User = userRepository.findById(1L);
        Optional<UserEntity> id2User = userRepository.findById(2L);

        // THEN
        assertThat(id1User.isPresent()).isTrue();
        assertThat(id2User.isPresent()).withFailMessage("id2 저장 트랜잭션내부에서 RuntimeException이 발생하여 저장되지 않아야한다.").isFalse();
    }
}
