## zk2nacos-sync
dubbo注册中心从zk迁移到nacos。<br/>
### 动机-为解放双手而生
在阿里开源的[nacos-sync](https://github.com/nacos-group/nacos-sync)工具中，如果要将dubbo中的服务全部迁移到
nacos中，需要一个个的去复制粘贴，而且创建同步的时候，`group`, `version`必须一一对应，否则同步不成功。为了简化这个操作，
先通过程序读取zk节点的方式然后解析相应的服务，再插入到`task`表中。不用再手动在web界面上创建同步任务。