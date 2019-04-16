import React, { Component } from "react";
import axios from "axios";
import { Icon, message, Spin, Table } from "antd";
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
  }
];

export default class File extends Component {
  state = {
    isContentLoaded: false,
    isContentShow: false,
    file: {
      createdDate: "",
      updatedDate: ""
    },
    content: {
      items: []
    }
  };

  async componentDidMount() {
    this.fetchFileInfo();
  }

  fetchFileInfo = async () => {
    const {
      location: {
        state: { projectId, fileId }
      }
    } = this.props;
    const { data } = await axios.get(
      `/api/projects/${projectId}/files/${fileId}`
    );
    this.setState({ file: data });
  };

  fetchFileContent = async () => {
    const {
      location: {
        state: { projectId, fileId }
      }
    } = this.props;
    const { data } = await axios.get(
      `/api/projects/${projectId}/files/${fileId}/content`
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

  render() {
    const {
      location: {
        state: { projectId, fileId }
      }
    } = this.props;
    const { file, content, isContentLoaded, isContentShow } = this.state;

    const mainInfoData = [
      {
        key: "1",
        created: file.createdDate,
        updated: file.updatedDate,
      }
    ];

    return (
      <div className="container">
        <div>
          <h2>File № {fileId}</h2>
        </div>
        <div>
          <div>
            <Table
              className="file__info"
              columns={mainInfoColumns}
              dataSource={mainInfoData}
              pagination={false}
            />
            <div className="file__file-content">
              <div
                className="file__file-show-content"
                onClick={this.onToggleShowContent}
              >
                <b>Content</b> <Icon type={isContentShow ? "up" : "down"} />{" "}
              </div>
              <div
                className="file__file-content-info"
                style={{
                  visibility: isContentShow ? "visible" : "hidden"
                }}
              >
                {isContentLoaded ? (
                  <div style={{ margin: "12px 8px 0", overflow: "initial" }}>
                    {content.items.map((item, index, { length }) => (
                      <div
                        key={index}
                        style={{
                          padding: 6,
                          background: "#fff",
                          textAlign: "left"
                        }}
                      >
                        {item}
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="file__file-content-loader">
                    <Spin size="large" />
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }
}
