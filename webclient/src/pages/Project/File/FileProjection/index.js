import React, { Component } from "react";
import axios from "axios";
import { Table, Tag } from "antd";
import CodeEditor from "components/CodeEditor";
import "./FileProjection.sass";

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
    }
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

  render() {
    const {
      match: {
        params: { commitId }
      }
    } = this.props;
    const {
      file: { metadata, description, content }
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
        <div className="start-xs">
          File: {metadata.name} Change: {commitId}
        </div>
        <div>
          <div className="file__metadata">
            <div className="row file__metadata-header">
              <div className="col-xs-3">
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
              <div className="col-xs-3">
                <b>Description</b>
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
          <div className="file__content">
            <div className="row file__content-header">
              <div className="col-xs-3">
                <b>Content</b>
              </div>
              <div className="col-xs-9" />
            </div>
            <CodeEditor
              className="file__content-info"
              content={content}
              readOnly={true}
              onUpdateContent={() => {}}
            />
          </div>
        </div>
      </div>
    );
  }
}
