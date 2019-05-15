import React, { Component } from 'react';
import axios from 'axios';
import debounce from 'lodash/debounce';
import { Link } from 'react-router-dom';
import _isEmpty from 'lodash/isEmpty';
import { Button, Icon, Spin, Table, Tag, message, Modal, Input } from 'antd';
import CodeEditor from 'components/CodeEditor';
import HistoryChanges from 'components/HistoryChanges';
import MultiEdit from 'components/MultiEdit';
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

const metadataColumns = [
  {
    title: 'Name',
    dataIndex: 'name',
    key: 'name',
    width: 3
  },
  {
    title: 'Authors',
    key: 'authors',
    dataIndex: 'authors',
    width: 3,
    render: authors => (
      <span>
        {authors.map(author => {
          return (
            <Tag color="geekblue" key={author}>
              {author}
            </Tag>
          );
        })}
      </span>
    )
  },
  {
    title: 'Organizations',
    key: 'organizations',
    dataIndex: 'organizations',
    width: 2,
    render: organizations => (
      <span>
        {organizations.map(organization => {
          return (
            <Tag color="geekblue" key={organization}>
              {organization.toUpperCase()}
            </Tag>
          );
        })}
      </span>
    )
  },
  {
    title: 'Originating system',
    dataIndex: 'originatingSystem',
    key: 'originatingSystem',
    width: 2
  },
  {
    title: 'Preprocessor version',
    dataIndex: 'preprocessorVersion',
    key: 'preprocessorVersion',
    width: 2
  }
];

const descriptionColumns = [
  {
    title: 'Descriptions',
    key: 'descriptions',
    dataIndex: 'descriptions',
    width: 9,
    render: descriptions => (
      <span>
        {descriptions.map(description => {
          return <div key={description}>{description}</div>;
        })}
      </span>
    )
  },
  {
    title: 'Implementation level',
    dataIndex: 'implementationLevel',
    key: 'implementationLevel',
    width: 3
  }
];

export default class File extends Component {
  state = {
    isContentLoaded: false,
    isContentShow: false,
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
    this.setState({ file: data });
  };

  handleEditDescr = modalVisible => {
    const { newDescription } = this.state;
    const {
      match: {
        params: { projectId, fileId }
      }
    } = this.props;
    axios
      .put(`/api/projects/${projectId}/files/${fileId}/description`, {
        descriptions: [newDescription],
        implementationLevel: this.state.file.description.implementationLevel
      })
      .then(() => {
        this.setState(
          {
            [modalVisible]: false
          },
          () => {
            this.fetchFileInfo();
            this.fetchFileHistoryChanges();
            message.success('Descr was updated');
          }
        );
      });
  };

  onChangeFileDescription = (event, data) => {
    this.setState({
      newDescription: event.target.value
    });
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
    console.log(contentState);
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
      visibleEditDescr
    } = this.state;

    const mainInfoData = [
      {
        key: '1',
        created: file.createdDate,
        updated: file.updatedDate,
        schema: file.schema
      }
    ];
    const metadataData = [
      {
        key: '1',
        name: file.metadata.name,
        authors: file.metadata.authors,
        organizations: file.metadata.organizations,
        originatingSystem: file.metadata.originatingSystem,
        preprocessorVersion: file.metadata.preprocessorVersion
      }
    ];
    const descriptionData = [
      {
        key: '1',
        descriptions: file.description.descriptions,
        implementationLevel: file.description.implementationLevel
      }
    ];
    return (
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
            <div className="file__metadata">
              <div className="row file__metadata-header">
                <div className="col-xs-3" style={{ textAlign: 'left', marginBottom: '4px' }}>
                  <b>Metadata</b>
                </div>
                <div className="col-xs-9" />
              </div>
              <div className="file__metadata-info">
                <Table
                  className="file__metadata-table"
                  columns={metadataColumns}
                  dataSource={metadataData}
                  pagination={false}
                />
              </div>
            </div>
            <div className="file__description">
              <div className="row file__description-header">
                <div className="col-xs-3" style={{ textAlign: 'left', marginBottom: '4px' }}>
                  <b>Description</b>
                  <Button
                    type="primary"
                    style={{ marginLeft: 8, alignContent: 'right' }}
                    onClick={() => {
                      this.setEditedDescr(file.description.descriptions[0]);
                      this.showModal('visibleEditDescr');
                    }}
                  >
                    <Icon type={'edit'} />
                  </Button>
                </div>
                <div className="col-xs-9" />
              </div>
              <div className="file__description-info">
                <Table
                  className="file__description-table"
                  columns={descriptionColumns}
                  dataSource={descriptionData}
                  pagination={false}
                />
              </div>
            </div>
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
    );
  }
}
