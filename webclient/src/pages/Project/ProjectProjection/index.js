import React, { Component } from 'react';
import axios from 'axios';
import { Card, Table, Tag } from 'antd';
import CodeEditor from 'components/CodeEditor';
import FileMetadata from 'pages/Project/File/FileMetadata';
import FileDescr from 'pages/Project/File/FileDescr';
import './ProjectProjection.sass';

export default class ProjectProjection extends Component {
  state = {
    project: {
      name: '',
      description: '',
      files: [
        {
          metadata: {
            name: '',
            authors: [],
            organizations: []
          },
          description: {
            descriptions: []
          },
          content: ''
        }
      ]
    }
  };

  async componentDidMount() {
    this.fetchProjectProjection();
  }

  fetchProjectProjection = async () => {
    const {
      match: {
        params: { projectId, commitId }
      }
    } = this.props;
    const { data } = await axios.get(`/api/projects/${projectId}/commits/${commitId}/content`);
    this.setState({ project: data });
  };

  renderFileProjection = file => {
    const { metadata, description, content } = file;

    return (
      <Card type="inner" style={{ marginTop: 16 }} title={metadata.name}>
        <FileMetadata
          metadata={metadata}
          match={this.props.match}
          readOnly={true}
        />
        <FileDescr
          description={description}
          match={this.props.match}
          readOnly={true}
        />
        <div className="project__file-content">
          <div className="row project__file-content-header">
            <div
              className="col-xs-3"
              style={{ textAlign: 'left', marginBottom: '10px', marginTop: '10px' }}
            >
              <b>Content</b>
            </div>
            <div className="col-xs-9" />
          </div>
          <div className="project__file-content-info">
            {content && <CodeEditor content={content} readOnly={true} onUpdateContent={() => {}} />}
          </div>
        </div>
      </Card>
    );
  };

  render() {
    const {
      match: {
        params: { commitId }
      }
    } = this.props;
    const {
      project: { name, description, files }
    } = this.state;

    return (
      <div className="container">
        <div>
          <h2>Project projection</h2>
        </div>
        <div className="start-xs">
          Project: {name} Change: {commitId}
        </div>
        <div className="row project__description">
          <div className="project__description-header col-xs-2 start-xs">Description</div>
          <div className="project__description-info col-xs-10 start-xs">{description}</div>
        </div>
        <div className="project__files">
          <div className="row project__files-header">
            <div
              className="col-xs-3"
              style={{ textAlign: 'left', marginBottom: '10px', marginTop: '10px' }}
            >
              <b>Files</b>
            </div>
            <div className="col-xs-9" />
          </div>
          <Card className="project__files-list">
            {files.map(file => this.renderFileProjection(file))}
          </Card>
        </div>
      </div>
    );
  }
}
