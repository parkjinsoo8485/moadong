package moadong.util.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * 테스트 환경에서는 Embedded MongoDB가 standalone(비-replica set)으로 실행되므로
 * Spring Mongo 트랜잭션을 사용하면 'Transaction numbers are only allowed on a replica
 * set member or mongos' 오류가 발생한다.
 *
 * 이 설정은 test 프로파일에서 MongoTransactionManager 대신 no-op
 * AbstractPlatformTransactionManager를 사용하여 @Transactional 메서드가
 * 실제 Mongo 트랜잭션 명령 없이 실행되도록 한다.
 */
@TestConfiguration
@Profile("test")
public class TestTransactionConfig {

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager() {
        return new AbstractPlatformTransactionManager() {
            @Override
            protected Object doGetTransaction() {
                return new Object();
            }

            @Override
            protected void doBegin(Object transaction, TransactionDefinition definition) {
                // no-op
            }

            @Override
            protected void doCommit(DefaultTransactionStatus status) {
                // no-op
            }

            @Override
            protected void doRollback(DefaultTransactionStatus status) {
                // no-op
            }
        };
    }
}

