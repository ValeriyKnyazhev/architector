import React, { Component } from "react";
import axios from "axios";
import debounce from "lodash/debounce";
import { Link } from "react-router-dom";
import { Button, Icon, Modal, Spin, Table } from "antd";
import CodeEditor from "components/CodeEditor";
import HistoryChanges from "components/HistoryChanges";
import FileMetadata from "./FileMetadata";
import FileDescription from "./FileDescription";
import "./File.sass";

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
    title: "Schema",
    dataIndex: "schema",
    key: "schema",
    width: 4
  }
];

export default class File extends Component {
  state = {
    isContentLoaded: false,
    isContentShow: false,
    fileDataLoaded: false,
    file: {
      createdDate: "",
      updatedDate: "",
      schema: "",
      metadata: {
        authors: [],
        organizations: []
      },
      description: {
        descriptions: []
      },
      currentCommitId: 0
    },
    historyChanges: [],
    content: "",
    contentReadOnly: true,
    updatedContent: ""
  };
  onUpdateContent = debounce(contentState => {
    this.setState({
      updatedContent: String(contentState)
    });
  }, 1000);

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
    this.setState({
      file: data,
      fileDataLoaded: true,
      isContentLoaded: false,
      isContentShow: false
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
    const {
      file: { currentCommitId }
    } = this.state;
    const { data } = await axios.get(
      `/api/projects/${projectId}/files/${fileId}/commits/${currentCommitId}/content`
    );
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
    const {
      file: { currentCommitId }
    } = this.state;

    axios
      .put(`/api/projects/${projectId}/files/${fileId}/content`, {
        commitMessage: "update file",
        content: this.state.updatedContent,
        headCommitId: currentCommitId
      })
      .then(response => {
        if (response.data.conflictBlocks) {
          this.props.history.push({
            pathname: "/conflict",
            state: { conflictData: response.data, projectId: projectId, fileId: fileId }
          });
        } else if (response.data.invalidEntities) {
          Modal.error({
            title: "Your changes affected the following objects. Please, fix.",
            content: (
              <div>
                {response.data.invalidEntities.map(item => {
                  return (
                    <div>
                      #{item.id} {item.name}
                    </div>
                  );
                })}
              </div>
            )
          });
        } else {
          const { updatedRoots } = response.data;
          this.fetchFileInfo();
          this.fetchFileHistoryChanges();
          this.onEditContent();
          Modal.success({
            title: "Your changes are applied. And the following root entities affected.",
            content: (
              <div>
                {updatedRoots.map(root => {
                  return (
                    <div>
                      #{root.id} {root.name}
                    </div>
                  );
                })}
              </div>
            )
          });
        }
      });
  };

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
      fileDataLoaded
    } = this.state;

    const mainInfoData = [
      {
        key: "1",
        created: file.createdDate,
        updated: file.updatedDate,
        schema: file.schema
      }
    ];

    const readOnly = !(file.accessRights === "OWNER" || file.accessRights === "WRITE");

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
              headCommitId={file.currentCommitId}
              metadata={file.metadata}
              fetchFileInfo={this.fetchFileInfo}
              fetchFileHistoryChanges={this.fetchFileHistoryChanges}
              match={this.props.match}
              readOnly={readOnly}
            />
            <FileDescription
              headCommitId={file.currentCommitId}
              description={file.description}
              fetchFileInfo={this.fetchFileInfo}
              fetchFileHistoryChanges={this.fetchFileHistoryChanges}
              match={this.props.match}
              readOnly={readOnly}
            />
            <div className="file__file-content">
              <div className="file__file-show-content" onClick={this.onToggleShowContent}>
                <b>Content</b> <Icon type={isContentShow ? "up" : "down"}/>{" "}
              </div>
              <div
                className="file__file-content-edit"
                style={{
                  visibility: isContentShow ? "visible" : "hidden"
                }}
              >
                {!readOnly && (
                  <Button
                    type="primary"
                    style={{ marginBottom: 16, alignContent: "right" }}
                    onClick={this.onEditContent}
                  >
                    <Icon type={contentReadOnly ? "edit" : "close"}/>
                  </Button>
                )}
              </div>
              <div
                className="file__file-content-save"
                style={{
                  visibility: isContentShow && !contentReadOnly ? "visible" : "hidden"
                }}
              >
                <Button
                  type="primary"
                  style={{ marginBottom: 16, alignContent: "right" }}
                  onClick={() => this.onSaveContent()}
                >
                  <Icon type={"save"}/>
                </Button>
              </div>
              <div
                className="file__file-content-info"
                style={{
                  visibility: isContentShow ? "visible" : "hidden"
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
                    <Spin size="large"/>
                  </div>
                )}
              </div>
            </div>
            <div className="file__changes">
              <div
                className="row file__changes-header"
                style={{ textAlign: "left", marginBottom: "4px" }}
              >
                <div className="file__changes-header-title col-xs-3 start-xs">
                  <b>Last changes</b>
                </div>
                <div className="col-xs-9 end-xs">
                  <Button
                    className="file__changes-show-more "
                    type="primary"
                    style={{ marginLeft: 8, alignContent: "right" }}
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
              <HistoryChanges commits={historyChanges}/>
            </div>
          </div>
        </div>
      </div>
    ) : (
      <Spin/>
    );
  }
}
