import React, { Component } from 'react';
import axios from 'axios';
import { Table, Tag, Icon, Spin, Card, Button } from 'antd';
import CodeEditor from 'components/CodeEditor';
import FileMetadata from 'pages/Project/File/FileMetadata';
import FileDescription from 'pages/Project/File/FileDescription';
import './FileProjection.sass';

export default class FileProjection extends Component {
  state = {
    file: {
      metadata: {
        name: '',
        authors: [],
        organizations: []
      },
      description: {
        descriptions: []
      },
      content: ''
    },
    isContentShow: false
  };

  async componentDidMount() {
    this.fetchFileProjection();
  }

  fetchFileProjection = async () => {
    const {
      match: {
        params: { projectId, fileId, commitId }
      }
    } = this.props;
    const { data } = await axios.get(
      `/api/projects/${projectId}/files/${fileId}/commits/${commitId}/content`
    );
    this.setState({ file: data });
  };

  onToggleShowContent = () => {
    const { isContentShow } = this.state;
    this.setState({ isContentShow: !isContentShow });
  };

  render() {
    const {
      match: {
        params: { projectId, fileId, commitId }
      }
    } = this.props;
    const {
      file: { metadata, description, content },
      isContentShow
    } = this.state;

    const metadataData = [
      {
        key: '1',
        name: metadata.name,
        authors: metadata.authors,
        organizations: metadata.organizations,
        originatingSystem: metadata.originatingSystem,
        preprocessorVersion: metadata.preprocessorVersion
      }
    ];
    const descriptionData = [
      {
        key: '1',
        descriptions: description.descriptions,
        implementationLevel: description.implementationLevel
      }
    ];

    return (
      <div className="container">
        <div>
          <h2>File projection</h2>
        </div>
        <div className="row file__header">
          <div className="file__header-info start-xs">
            File: {metadata.name} Change: {commitId}
          </div>

          <Button
            className="file__header-download-file"
            href={`/api/projects/${projectId}/files/${fileId}/changes/${commitId}/download`}
          >
            <Icon type="download" />
          </Button>
        </div>
        <div>
          <FileMetadata metadata={metadata} match={this.props.match} readOnly={true} />
          <FileDescription description={description} match={this.props.match} readOnly={true} />
          <div className="file__content">
            <div className="row file__content-header">
              <div
                className="col-xs-3"
                style={{ textAlign: 'left', marginBottom: '10px', marginTop: '10px' }}
              >
                <b>Content</b>
              </div>
              <div className="col-xs-9" />
            </div>
            <div className="file__file-content">
              <div className="file__file-show-content" onClick={this.onToggleShowContent}>
                <b>Content</b> <Icon type={isContentShow ? 'up' : 'down'} />{' '}
              </div>
              {isContentShow && (
                <div className="file__content-info">
                  {content ? (
                    <CodeEditor content={content} readOnly={true} onUpdateContent={() => {}} />
                  ) : (
                    <div className="file__file-content-loader">
                      <Spin size="large" />
                    </div>
                  )}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    );
  }
}
