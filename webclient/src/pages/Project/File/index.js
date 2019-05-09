import React, { Component } from 'react';
import axios from 'axios';
import { Icon, Spin, Table, Tag } from 'antd';
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

function buildMarkupContent(content) {
  return { __html: content };
}

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
    content: ''
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
    const { data } = await axios.get(`/api/projects/${projectId}/files/${fileId}`);
    this.setState({ file: data });
  };

  fetchFileContent = async () => {
    const {
      location: {
        state: { projectId, fileId }
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

  render() {
    const {
      location: {
        state: { fileId }
      }
    } = this.props;
    const { file, content, isContentLoaded, isContentShow } = this.state;

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
            <div className="file__file-content">
              <div className="file__file-show-content" onClick={this.onToggleShowContent}>
                <b>Content</b> <Icon type={isContentShow ? 'up' : 'down'} />{' '}
              </div>
              <div
                className="file__file-content-info"
                style={{
                  visibility: isContentShow ? 'visible' : 'hidden'
                }}
              >
                {isContentLoaded ? (
                  <div
                    style={{
                      margin: '12px 8px 0',
                      overflow: 'initial',
                      textAlign: 'left'
                    }}
                    dangerouslySetInnerHTML={buildMarkupContent(content)}
                  />
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
