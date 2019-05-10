import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import _isEmpty from 'lodash/isEmpty';
import { Button, Icon, Input, message, Modal, Popconfirm, Radio, Table, Upload } from 'antd';
import HistoryChanges from 'components/HistoryChanges';
import './Project.sass';

const RadioGroup = Radio.Group;

function constructSourceUrl(value) {
  return value.startsWith('https://') || value.startsWith('http://') ? value : 'https://' + value;
}

const mainInfoColumns = [
  {
    title: 'Created',
    dataIndex: 'created',
    key: 'created',
    width: 4,
    render: date => <div>{date && new Date(date).toLocaleDateString()}</div>
  },
  {
    title: 'Updated',
    dataIndex: 'updated',
    key: 'updated',
    width: 4,
    render: date => <div>{date && new Date(date).toLocaleDateString()}</div>
  },
  {
    title: 'Author',
    dataIndex: 'author',
    key: 'author',
    width: 4
  }
];

export default class Project extends Component {
  state = {
    project: {
      createdDate: '',
      updatedDate: '',
      projectName: '',
      author: '',
      description: '',
      files: []
    },
    historyChanges: [],
    file: null,
    uploading: false,
    uploadType: 'link',
    newFileSourceUrl: '',
    confirmLoading: false,
    visibleCreateFile: false,
    visiblePopup: false
  };

  async componentDidMount() {
    this.fetchProject.call(this);
    this.fetchProjectHistoryChanges.call(this);
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

  async fetchProjectHistoryChanges() {
    const {
      match: {
        params: { projectId }
      }
    } = this.props;
    const { data } = await axios.get(`/api/projects/${projectId}/commits`);
    this.setState({ historyChanges: data.commits });
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
            message.success('File was created');
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

  onChangeUploadType = e => {
    this.setState({
      uploadType: e.target.value
    });
  };

  handleUploadFile = modalVisible => {
    const { file } = this.state;
    const {
      match: {
        params: { projectId }
      }
    } = this.props;
    const formData = new FormData();
    formData.append('file', file);

    this.setState({
      confirmLoading: true
    });

    axios
      .post(`/api/projects/${projectId}/files/import`, formData)
      .then(() =>
        this.setState(
          {
            [modalVisible]: false,
            file: null,
            confirmLoading: false
          },
          message.success('upload successfully.')
        )
      )
      .catch(() =>
        this.setState(
          {
            file: null,
            confirmLoading: false,
            [modalVisible]: false
          },
          message.success('upload failed.')
        )
      );
  };

  render() {
    const {
      match: {
        params: { projectId }
      }
    } = this.props;
    const { project, historyChanges, confirmLoading, visibleCreateFile } = this.state;

    const mainInfoData = [
      {
        key: '1',
        created: project.createdDate,
        updated: project.updatedDate,
        author: project.author
      }
    ];
    const { file } = this.state;
    const props = {
      onRemove: file => {
        this.setState(state => {
          return {
            file: null
          };
        });
      },
      beforeUpload: file => {
        this.setState(state => ({
          file: file
        }));
        return false;
      },
      file
    };

    const filesListColumns = [
      {
        title: 'Name',
        dataIndex: 'name',
        key: 'filename',
        width: 4,
        render: (name, record) => {
          return (
            record.identifier && (
              <Link
                to={{
                  pathname: `/files/${record.identifier}`,
                  state: {
                    projectId: projectId,
                    fileId: record.identifier
                  }
                }}
              >
                {name}
              </Link>
            )
          );
        }
      },
      {
        title: 'Created',
        dataIndex: 'created',
        key: 'created',
        width: 4,
        render: date => <div>{date && new Date(date).toLocaleDateString()}</div>
      },
      {
        title: 'Updated',
        dataIndex: 'updated',
        key: 'updated',
        width: 4,
        render: date => <div>{date && new Date(date).toLocaleDateString()}</div>
      }
    ];
    const filesListData = project.files.map((file, index) => {
      return {
        key: index,
        identifier: file.fileId,
        name: file.name,
        created: file.createdDate,
        updated: file.updatedDate
      };
    });

    return (
      <div className="container">
        <div>
          <h2>Project {project.projectName}</h2>
        </div>
        <div>
          <div className="row project__description">
            <div className="project__description-header col-xs-3 start-xs">Description</div>
            <div className="project__description-info col-xs-9 start-xs">{project.description}</div>
          </div>
          <Table
            className="project__info"
            columns={mainInfoColumns}
            dataSource={mainInfoData}
            pagination={false}
          />
          <div className="project__files">
            <div className="row project__files-header">
              <div className="project__files_header-title col-xs-3 start-xs">Files</div>
              <div className="col-xs-9 end-xs">
                <Button
                  className="project__files-create-file "
                  onClick={() => this.showModal('visibleCreateFile')}
                  type="primary"
                  style={{ marginBottom: 16, alignContent: 'right' }}
                >
                  Add file <Icon type="plus-circle" />
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
                    this.state.uploadType === 'link'
                      ? this.handleCreateFileFromSource('visibleCreateFile')
                      : this.handleUploadFile('visibleCreateFile')
                  }
                  confirmLoading={confirmLoading}
                  onCancel={() => this.handleCancel('visibleCreateFile')}
                  okButtonProps={{
                    disabled:
                      this.state.uploadType === 'link'
                        ? _isEmpty(this.state.newFileSourceUrl)
                        : file === null
                  }}
                >
                  <>
                    <div style={{ marginBottom: 16 }}>
                      <RadioGroup onChange={this.onChangeUploadType} value={this.state.uploadType}>
                        <Radio value={'link'}>Link</Radio>
                        <Radio value={'file'}>File</Radio>
                      </RadioGroup>
                    </div>
                    {this.state.uploadType === 'link' && (
                      <div style={{ marginBottom: 16 }}>
                        <Input
                          placeholder="Enter your source URL"
                          value={this.state.newFileSourceUrl}
                          onChange={this.onChangeSourceUrl}
                          addonBefore="Https://"
                        />
                      </div>
                    )}
                    {this.state.uploadType === 'file' && (
                      <div>
                        <Upload {...props}>
                          <Button>
                            <Icon type="upload" /> Select File
                          </Button>
                        </Upload>
                      </div>
                    )}
                  </>
                </Modal>
              )}
              <Popconfirm
                title="Do you want to create new project?"
                visible={this.state.visiblePopup}
                onConfirm={() => this.handleCarded('visibleProjects')}
                onCancel={this.handleClosePopup}
                okText="Yes"
                cancelText="No"
              />
            </div>
          </div>
          <div className="project__changes">
            <div className="row project__changes-header">
              <div className="project__changes-header-title col-xs-3 start-xs">Last changes</div>
              <div className="col-xs-9 end-xs">
                <Button
                  className="project__changes-show-more "
                  type="primary"
                  style={{ marginBottom: 16, alignContent: 'right' }}
                >
                  Show more
                </Button>
              </div>
            </div>
            <HistoryChanges commits={historyChanges} />
          </div>
        </div>
      </div>
    );
  }
}
