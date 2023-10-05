package com.fyp.dhumbal.game.dal;

import com.fyp.dhumbal.game.dal.GameEntity;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends KeyValueRepository<GameEntity, String> {
}
