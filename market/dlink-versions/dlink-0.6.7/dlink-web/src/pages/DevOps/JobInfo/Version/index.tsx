import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {TaskVersion} from "@/pages/DevOps/data";
import React, {useRef, useState,} from "react";
import {queryData} from "@/components/Common/crud";
import {getIcon} from "@/components/Studio/icon";
import {Button, Modal, Tag} from "antd";
import {FullscreenOutlined} from "@ant-design/icons";
import CodeShow from "@/components/Common/CodeShow";

const url = '/api/task/version';
const TaskVersionInfo = (props: any) => {
  const {job} = props;
  const actionRef = useRef<ActionType>();
  const [row, setRow] = useState<TaskVersion>();
  const [modalVisible, setModalVisible] = useState<boolean>(false);



  const cancelHandle = () => {
    setRow(undefined);
    setModalVisible(false);
  }

  const handleShowStatement = (statement: string) =>{
    return (
      <div style={{width: "1100px"}}>
        <Modal title="作业执行 SQL" visible={modalVisible} destroyOnClose={true} width={"60%"}
         onCancel={()=>{
           cancelHandle();
         }}
         footer={[
           <Button key="back" onClick={() => {
             cancelHandle();
           }}>
             关闭
           </Button>,
         ]}>
          <CodeShow language={"sql"} code={statement} height={'600px'} />
        </Modal>
      </div>
    )

  }



  const columns: ProColumns<TaskVersion>[] = [
    {
      title: '作业ID',
      align: 'center',
      dataIndex: 'taskId',
      hideInSearch: true,
    },
    {
      title: '作业名称',
      align: 'center',
      sorter: true,
      dataIndex: 'name',
    },
    {
      title: '作业别名',
      align: 'center',
      sorter: true,
      dataIndex: 'alias',
    },
    {
      title: '作业方言',
      align: 'center',
      render: (dom, entity) => {
        return <>
            {getIcon(entity.dialect) }
            {
            <Tag color="blue">
              {entity.dialect}
            </Tag>
            }
        </>;
      },
    },
    {
      title: '作业类型',
      align: 'center',
      render: (dom, entity) => {
        return <>
          {
            <Tag color="blue">
              {entity.type}
            </Tag>
          }
        </>;
      },
    },
    {
      title: '版本号',
      align: 'center',
      sorter: true,
      dataIndex: 'versionId',
    },
    {
      title: '作业内容',
      align: 'center',
      ellipsis: true,
      hideInSearch: true,
      render: (dom, entity) => {
        return <>
          {<>
            <a onClick={()=>{
              setRow(entity)
              setModalVisible(true);
            }}>
              <Tag color="green">
                <FullscreenOutlined title={"查看作业详情"}  />
              </Tag> 查看作业详情
            </a>
            {handleShowStatement(entity.statement)}
          </>
          }
        </>
        ;
      },
    },
    {
      title: '创建时间',
      align: 'center',
      sorter: true,
      valueType: 'dateTime',
      dataIndex: 'createTime',
    },
  ];

  return (
    <>
      <ProTable<TaskVersion>
        columns={columns}
        style={{width: '100%'}}
        request={(params, sorter, filter) => queryData(url, {taskId: job?.instance.taskId, ...params, sorter, filter})}
        actionRef={actionRef}
        rowKey="id"
        pagination={{
          pageSize: 15,
        }}
        bordered
        search={false}
        size="small"
      />
    </>
  )
};

export default TaskVersionInfo;
