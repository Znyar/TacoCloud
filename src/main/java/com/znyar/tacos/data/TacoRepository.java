package com.znyar.tacos.data;

import com.znyar.tacos.entity.Taco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface TacoRepository extends CrudRepository<Taco, String> {

    Page<Taco> findAll(Pageable pageable);

}
