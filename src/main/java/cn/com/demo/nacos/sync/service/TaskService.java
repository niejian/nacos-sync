package cn.com.demo.nacos.sync.service;

import java.util.List;

/**
 * @author: nj
 * @date: 2019-09-03:11:51
 */
public interface TaskService {
    /**
     * 获取zk dubbo节点的服务信息
     * @return
     */
    List<String> getDubboService() throws Exception ;

    /**
     * 将具体的服务信息添加到task列表中
     * @param serviceName
     * @param groupName
     * @param version
     */
    void addNacosSyncTask(String serviceName, String groupName, String version);
}
