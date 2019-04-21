import React, { Component } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import _isEmpty from "lodash/isEmpty";
import { Button, Icon, Input, message, Modal, Popconfirm, Table } from "antd";
import "./Project.sass";

function constructSourceUrl(value) {
  return value.startsWith("https://") || value.startsWith("http://")
    ? value
    : "https://" + value;
}

const mainInfoColumns = [
  {
    title: "Created",
    dataIndex: "created",
    key: "created",
    width: 4,
    render: date => <div>{date && new Date(date).toLocaleDateString()}</div>
  },
  {
    title: "Updated",
    dataIndex: "updated",
    key: "updated",
    width: 4,
    render: date => <div>{date && new Date(date).toLocaleDateString()}</div>
  },
  {
    title: "Author",
    dataIndex: "author",
    key: "author",
    width: 4
  }
];

export default class Project extends Component {
  state = {
    project: {
      createdDate: "",
      updatedDate: "",
      projectName: "",
      author: "",
      description: "",
      files: []
    },
    newFileSourceUrl: "",
    confirmLoading: false,
    visibleCreateFile: false,
    visiblePopup: false
  };

  async componentDidMount() {
    this.fetchProject.call(this);
  }

  async fetchProject() {
    const {
      match: {
        params: { projectId }
      }
    } = this.props;
    const { data } = await axios.get(`/api/projects/${projectId}`);
    this.setState({ project: data });
  }

  showModal = state => {
    this.setState({
      [state]: true
    });
  };

  onChangeSourceUrl = event => {
    this.setState({ newFileSourceUrl: event.target.value });
  };

  handleCreateFileFromSource = modalVisible => {
    const { newFileSourceUrl } = this.state;
    const {
      match: {
        params: { projectId }
      }
    } = this.props;
    this.setState({
      confirmLoading: true
    });
    axios
      .post(`/api/projects/${projectId}/files/source`, {
        sourceUrl: constructSourceUrl(newFileSourceUrl)
      })
      .then(() => {
        this.setState(
          {
            [modalVisible]: false,
            confirmLoading: false
          },
          () => {
            this.fetchProject.call(this);
            message.success("File was created");
          }
        );
      });
  };

  handlePopup = () => {
    this.setState({ visiblePopup: true });
  };

  handleCancel = state => {
    this.setState({
      [state]: false
    });
  };

  handleClosePopup = () => {
    this.setState({
      visiblePopup: false
    });
  };

  render() {
    const {
      match: {
        params: { projectId }
      }
    } = this.props;
    const { project, confirmLoading, visibleCreateFile } = this.state;

    const mainInfoData = [
      {
        key: "1",
        created: project.createdDate,
        updated: project.updatedDate,
        author: project.author
      }
    ];

    const filesListColumns = [
      {
        title: "Identifier",
        dataIndex: "identifier",
        key: "identifier",
        width: 4,
        render: fileId => {
          return (
            fileId && (
              <Link
                to={{
                  pathname: `/files/${fileId}`,
                  state: {
                    projectId: projectId,
                    fileId: fileId
                  }
                }}
              >
                {fileId}
              </Link>
            )
          );
        }
      },
      {
        title: "Created",
        dataIndex: "created",
        key: "created",
        width: 4,
        render: date => <div>{date && new Date(date).toLocaleDateString()}</div>
      },
      {
        title: "Updated",
        dataIndex: "updated",
        key: "updated",
        width: 4,
        render: date => <div>{date && new Date(date).toLocaleDateString()}</div>
      }
    ];
    const filesListData = project.files.map((file, index) => {
      return {
        key: index,
        identifier: file.fileId,
        created: file.createdDate,
        updated: file.updatedDate
      };
    });

    return (
      <div className="container">
        <div>
          <h2>Project № {projectId}</h2>
        </div>
        <div>
          <Table
            className="project__info"
            columns={mainInfoColumns}
            dataSource={mainInfoData}
            pagination={false}
          />
          <div className="project__files">
            <div className="row project__files-header">
              <div className="project__files_header-title col-xs-3">
                <h4>Files</h4>
              </div>
              <div className="col-xs-9">
                <Button
                  className="project__files-create-file"
                  onClick={() => this.showModal("visibleCreateFile")}
                  type="primary"
                  style={{ marginBottom: 16, alignContent: "right" }}
                >
                  Add file <Icon type="plus-circle"/>
                </Button>
              </div>
            </div>
            <Table
              className="project__files-table"
              bordered
              columns={filesListColumns}
              dataSource={filesListData}
            />
            <div className="project__files-create-file-modal">
              {visibleCreateFile && (
                <Modal
                  title="Add new file"
                  visible={visibleCreateFile}
                  onOk={() =>
                    this.handleCreateFileFromSource("visibleCreateFile")
                  }
                  confirmLoading={confirmLoading}
                  onCancel={() => this.handleCancel("visibleCreateFile")}
                  okButtonProps={{
                    disabled: _isEmpty(this.state.newFileSourceUrl)
                  }}
                >
                  <div style={{ marginBottom: 16 }}>
                    <Input
                      placeholder="Enter your source URL"
                      value={this.state.newFileSourceUrl}
                      onChange={this.onChangeSourceUrl}
                      addonBefore="Https://"
                    />
                  </div>
                </Modal>
              )}
              <Popconfirm
                title="Do you want to create new project?"
                visible={this.state.visiblePopup}
                onConfirm={() => this.handleCarded("visibleProjects")}
                onCancel={this.handleClosePopup}
                okText="Yes"
                cancelText="No"
              />
            </div>
          </div>
        </div>
      </div>
    );
  }
}
