package cn.com.demo.nacos.sync.service.impl;

import cn.com.demo.nacos.sync.dao.TaskReposity;
import cn.com.demo.nacos.sync.entiy.TaskEntity;
import cn.com.demo.nacos.sync.service.TaskService;
import org.I0Itec.zkclient.ZkClient;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author: nj
 * @date: 2019-09-03:11:56
 */

@Service(value = "taskService")
public class TaskServiceImpl implements TaskService {
    private Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    @Value("${zookeeper.address}")
    private String zkAddress;
    @Value("${sync.dest-cluster-id}")
    private String destClusterId;
    @Value("${sync.source-cluster-id}")
    private String sourceClusterId;
    private static final String DUBBO_PREFIX = "/dubbo";
    private static final String DUBBO_PROVIDRES = "/providers";
    private volatile static ZkClient zkClient = null;
    private static final String TASK_STATUS = "SYNC";

    @Autowired
    private TaskReposity taskReposity;


    /**
     * 获取zk dubbo节点的服务信息
     *
     * @return
     */
    @Override
    public List<String> getDubboService() throws Exception {
        List<String> serviceNames = this.getServiceName(DUBBO_PREFIX);
        taskReposity.deleteAll();

        // 循环获取providers信息新
        for (String serviceName : serviceNames) {

            if (StringUtils.isEmpty(serviceName)) {
                continue;
            }

            String providerPath = DUBBO_PREFIX + "/" + serviceName + DUBBO_PROVIDRES;
            List<String> providerUrls = this.getServiceName(providerPath);
            for (String providerUrl : providerUrls) {
                if (StringUtils.isEmpty(providerUrl)) {
                    continue;
                }
                //解码
//                providerUrl = URLDecoder.decode(providerUrl, "utf-8");
                Map<String, String> queryParam = cn.com.demo.nacos.sync.util.StringUtils.parseQueryString(providerUrl);
                logger.info("{}--->{}", serviceName, queryParam);
                String version = queryParam.containsKey("version") ? queryParam.get("version") : null;
                String groupName = queryParam.containsKey("group") ? queryParam.get("group") : null;
                this.addNacosSyncTask(serviceName, groupName, version);


            }


        }

        return null;
    }

    /**
     * 将具体的服务信息添加到task列表中
     *
     * @param serviceName
     * @param groupName
     * @param version
     */
    @Override
    public void addNacosSyncTask(String serviceName, String groupName, String version) {
        //查询有没有数据
        List<TaskEntity> list = this.taskReposity.getTaskEntitiesByGroupNameAndVersionAndServiceName(groupName, version, serviceName);
        if (!CollectionUtils.isEmpty(list)) {
            return;
        }
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setDestClusterId(destClusterId);
        taskEntity.setWorkerIp("sys");
        taskEntity.setVersion(version);
        taskEntity.setTaskStatus(TASK_STATUS);
        taskEntity.setSourceClusterId(sourceClusterId);
        taskEntity.setServiceName(serviceName);
        taskEntity.setOperationId(UUID.randomUUID().toString());
        taskEntity.setTaskId(UUID.randomUUID().toString());
        taskEntity.setGroupName(groupName);


        taskReposity.save(taskEntity);
    }

    @PostConstruct
    public void init() {
        if (null == zkClient) {
            zkClient = new ZkClient(zkAddress);
        }
    }

    /**
     * 获取所有服务名
     * @return
     */
    private List<String> getServiceName(String path) {
        List<String> serviceNames = zkClient.getChildren(path);
        if (null == serviceNames) {
            serviceNames = new ArrayList<>();
        }
        return serviceNames;
    }



}
