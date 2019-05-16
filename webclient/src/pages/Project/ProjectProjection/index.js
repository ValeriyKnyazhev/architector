import React, { Component } from 'react';
import axios from 'axios';
import { Card, Table, Tag } from 'antd';
import CodeEditor from 'components/CodeEditor';
import './ProjectProjection.sass';

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
    console.log(data);
    this.setState({ project: data });
  };

  renderFileProjection = file => {
    const { metadata, description, content } = file;

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
      <Card type="inner" style={{ marginTop: 16 }} title={metadata.name}>
        <div className="project__file-metadata">
          <div className="row project__file-metadata-header">
            <div
              className="col-xs-3"
              style={{ textAlign: 'left', marginBottom: '10px', marginTop: '10px' }}
            >
              <b>Metadata</b>
            </div>
            <div className="col-xs-9" />
          </div>
          <div className="project__file-metadata-info">
            <Table
              className="project__file-metadata-table"
              columns={metadataColumns}
              dataSource={metadataData}
              pagination={false}
            />
          </div>
        </div>
        <div className="project__file-description">
          <div className="row project__file-escription-header">
            <div
              className="col-xs-3"
              style={{ textAlign: 'left', marginBottom: '10px', marginTop: '10px' }}
            >
              <b>Description</b>
            </div>
            <div className="col-xs-9" />
          </div>
          <div className="project__file-description-info">
            <Table
              className="project__file-description-table"
              columns={descriptionColumns}
              dataSource={descriptionData}
              pagination={false}
            />
          </div>
        </div>
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
