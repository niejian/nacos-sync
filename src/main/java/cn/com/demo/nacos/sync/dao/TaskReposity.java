package cn.com.demo.nacos.sync.dao;

import cn.com.demo.nacos.sync.entiy.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: nj
 * @date: 2019-09-03:11:48
 */
@Repository
public interface TaskReposity extends JpaRepository<TaskEntity, Long> {

    List<TaskEntity> getTaskEntitiesByGroupNameAndVersionAndServiceName(String groupName, String version, String ServiceName);
}
