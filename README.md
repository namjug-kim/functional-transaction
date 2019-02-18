# functional-transaction

## How to use
```java
public void transactionExample() {
    Transaction.create(() -> {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(1L);
                    userEntity.setUserName("테스트유저");
                    return userRepository.save(userEntity);
                })
                .execute(transactionManager, Propagation.REQUIRES_NEW);
}
```