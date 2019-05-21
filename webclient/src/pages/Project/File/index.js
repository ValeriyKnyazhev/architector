import React, { Component } from 'react';
import axios from 'axios';
import debounce from 'lodash/debounce';
import { Link } from 'react-router-dom';
import _isEmpty from 'lodash/isEmpty';
import { Button, Icon, Spin, Table, message, Modal, Input } from 'antd';
import CodeEditor from 'components/CodeEditor';
import HistoryChanges from 'components/HistoryChanges';
import FileMetadata from './FileMetadata';
import FileDescr from './FileDescr';
import './File.sass';
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
    title: 'Schema',
    dataIndex: 'schema',
    key: 'schema',
    width: 4
  }
];

export default class File extends Component {
  state = {
    isContentLoaded: false,
    isContentShow: false,
    fileDataLoaded: false,
    file: {
      createdDate: '',
      updatedDate: '',
      schema: '',
      metadata: {
        authors: [],
        organizations: []
      },
      description: {
        descriptions: []
      }
    },
    historyChanges: [],
    content: '',
    contentReadOnly: true,
    updatedContent: '',
    visibleEditDescr: false,
    newDescription: ''
  };

  async componentDidMount() {
    this.fetchFileInfo();
    this.fetchFileHistoryChanges();
  }

  showModal = state => {
    this.setState({
      [state]: true
    });
  };

  handleCancel = state => {
    this.setState({
      [state]: false
    });
  };

  fetchFileInfo = async () => {
    const {
      match: {
        params: { projectId, fileId }
      }
    } = this.props;
    const { data } = await axios.get(`/api/projects/${projectId}/files/${fileId}`);
    this.setState({ file: data, fileDataLoaded: true });
  };

  fetchFileHistoryChanges = async () => {
    const {
      match: {
        params: { projectId, fileId }
      }
    } = this.props;
    const { data } = await axios.get(`/api/projects/${projectId}/files/${fileId}/commits`);
    this.setState({ historyChanges: data.commits });
  };

  fetchFileContent = async () => {
    const {
      match: {
        params: { projectId, fileId }
      }
    } = this.props;
    const { data } = await axios.get(`/api/projects/${projectId}/files/${fileId}/content`);
    this.setState({ content: data.content, isContentLoaded: true });
  };

  onToggleShowContent = () => {
    const { isContentShow, isContentLoaded } = this.state;
    if (!isContentShow) {
      this.setState({ isContentShow: true });
      !isContentLoaded && this.fetchFileContent();
    } else {
      this.setState({ isContentShow: false });
    }
  };

  onEditContent = () => {
    this.setState(prevState => ({
      contentReadOnly: !prevState.contentReadOnly
    }));
  };

  onSaveContent = () => {
    const {
      match: {
        params: { projectId, fileId }
      }
    } = this.props;

    axios
      .put(`/api/projects/${projectId}/files/${fileId}/content`, {
        commitMessage: 'update file',
        content: this.state.updatedContent
      })
      .then(() => {
        this.fetchFileHistoryChanges();
        this.onEditContent();
      });
  };

  setEditedDescr = description =>
    this.setState({
      newDescription: description
    });

  onUpdateContent = debounce(contentState => {
    this.setState({
      updatedContent: String(contentState)
    });
  }, 1000);

  render() {
    const {
      match: {
        params: { projectId, fileId }
      }
    } = this.props;
    const {
      file,
      historyChanges,
      content,
      isContentLoaded,
      isContentShow,
      contentReadOnly,
      visibleEditDescr,
      fileDataLoaded
    } = this.state;

    const mainInfoData = [
      {
        key: '1',
        created: file.createdDate,
        updated: file.updatedDate,
        schema: file.schema
      }
    ];

    return fileDataLoaded ? (
      <div className="container">
        <div>
          <h2>File: {file.metadata.name}</h2>
        </div>
        <div>
          <div>
            <Table
              className="file__info"
              columns={mainInfoColumns}
              dataSource={mainInfoData}
              pagination={false}
            />
            <FileMetadata
              file={file}
              fetchFileInfo={this.fetchFileInfo}
              fetchFileHistoryChanges={this.fetchFileHistoryChanges}
              match={this.props.match}
            />
            <FileDescr
              file={file}
              fetchFileInfo={this.fetchFileInfo}
              fetchFileHistoryChanges={this.fetchFileHistoryChanges}
              match={this.props.match}
            />
            <div className="file__file-content">
              <div className="file__file-show-content" onClick={this.onToggleShowContent}>
                <b>Content</b> <Icon type={isContentShow ? 'up' : 'down'} />{' '}
              </div>
              <div
                className="file__file-content-edit"
                style={{
                  visibility: isContentShow ? 'visible' : 'hidden'
                }}
              >
                <Button
                  type="primary"
                  style={{ marginBottom: 16, alignContent: 'right' }}
                  onClick={this.onEditContent}
                >
                  <Icon type={contentReadOnly ? 'edit' : 'close'} />
                </Button>
              </div>
              <div
                className="file__file-content-save"
                style={{
                  visibility: isContentShow && !contentReadOnly ? 'visible' : 'hidden'
                }}
              >
                <Button
                  type="primary"
                  style={{ marginBottom: 16, alignContent: 'right' }}
                  onClick={() => this.onSaveContent()}
                >
                  <Icon type={'save'} />
                </Button>
              </div>
              <div
                className="file__file-content-info"
                style={{
                  visibility: isContentShow ? 'visible' : 'hidden'
                }}
              >
                {isContentLoaded ? (
                  <CodeEditor
                    content={content}
                    readOnly={contentReadOnly}
                    onUpdateContent={this.onUpdateContent}
                  />
                ) : (
                  <div className="file__file-content-loader">
                    <Spin size="large" />
                  </div>
                )}
              </div>
            </div>
            <div className="file__changes">
              <div className="row file__changes-header">
                <div className="file__changes-header-title col-xs-3 start-xs">Last changes</div>
                <div className="col-xs-9 end-xs">
                  <Button
                    className="file__changes-show-more "
                    type="primary"
                    style={{ marginBottom: 16, alignContent: 'right' }}
                  >
                    <Link
                      to={{
                        pathname: `/projects/${projectId}/files/${fileId}/changes`
                      }}
                    >
                      Show more
                    </Link>
                  </Button>
                </div>
              </div>
              <HistoryChanges commits={historyChanges} />
            </div>
          </div>
        </div>
        {visibleEditDescr && (
          <Modal
            title="Edit file description"
            visible={visibleEditDescr}
            onOk={() => this.handleEditDescr('visibleEditDescr')}
            onCancel={() => this.handleCancel('visibleEditDescr')}
            okButtonProps={{
              disabled: _isEmpty(this.state.newDescription)
            }}
          >
            <div>
              <div className="projects__input-label">Description</div>
              <Input
                placeholder="Enter your project description"
                value={this.state.newDescription}
                onChange={e => this.onChangeFileDescription(e, 'newDescription')}
              />
            </div>
          </Modal>
        )}
      </div>
    ) : (
      <Spin />
    );
  }
}
