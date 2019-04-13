import React, { Component } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import _isEmpty from "lodash/isEmpty";
import { Button, Icon, Input, message, Table, Divider, Tag } from "antd";
import "./Project.sass";

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

const metadataColumns = [
  {
    title: "Name",
    dataIndex: "name",
    key: "name",
    width: 3
  },
  {
    title: "Authors",
    key: "authors",
    dataIndex: "authors",
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
    title: "Organizations",
    key: "organizations",
    dataIndex: "organizations",
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
    title: "Originating system",
    dataIndex: "originatingSystem",
    key: "originatingSystem",
    width: 2
  },
  {
    title: "Preprocessor version",
    dataIndex: "preprocessorVersion",
    key: "preprocessorVersion",
    width: 2
  }
];

const descriptionColumns = [
  {
    title: "Descriptions",
    key: "descriptions",
    dataIndex: "descriptions",
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
    title: "Implementation level",
    dataIndex: "implementationLevel",
    key: "implementationLevel",
    width: 3
  }
];

export default class Project extends Component {
  state = {
    project: {
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
      files: []
    }
  };

  async componentDidMount() {
    this.fetchProject.call(this);
  }

  async fetchProject() {
    const {
      match: {
        params: { projectId }
      }
    } = this.props;
    const { data } = await axios.get(`/api/projects/${projectId}`);
    data.metadata.authors = ["Valeriy", "Sofia", "Knyazhev", "Buyanova"];
    this.setState({ project: data });
  }

  render() {
    const {
      match: {
        params: { projectId }
      }
    } = this.props;
    const { project } = this.state;

    const mainInfoData = [
      {
        key: "1",
        created: project.createdDate,
        updated: project.updatedDate,
        schema: project.schema
      }
    ];
    const metadataData = [
      {
        key: "1",
        name: project.metadata.name,
        authors: project.metadata.authors,
        organizations: project.metadata.organizations,
        originatingSystem: project.metadata.originatingSystem,
        preprocessorVersion: project.metadata.preprocessorVersion
      }
    ];
    const descriptionData = [
      {
        key: "1",
        descriptions: project.description.descriptions,
        implementationLevel: project.description.implementationLevel
      }
    ];

    const filesListColumns = [
      {
        title: "Identifier",
        dataIndex: "identifier",
        key: "identifier",
        width: 4,
        render: fileId => {
          return (
            fileId && (
              <Link
                to={{
                  pathname: `/files/${fileId}`,
                  state: {
                    projectId: projectId,
                    fileId: fileId
                  }
                }}
              >
                {fileId}
              </Link>
            )
          );
        }
      },
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
    const filesListData = project.files.map((file, index) => {
      return {
        key: index,
        identifier: file.fileId,
        created: file.createdDate,
        updated: file.updatedDate
      };
    });

    return (
      <div className="container">
        <div>
          <h2>Project № {projectId}</h2>
        </div>
        <div>
          <Table
            className="project__info"
            columns={mainInfoColumns}
            dataSource={mainInfoData}
            pagination={false}
          />
          <div className="project__metadata">
            <div className="row project__metadata-header">
              <div className="col-xs-3">
                <b>Metadata</b>
              </div>
              <div className="col-xs-9" />
            </div>
            <div className="project__metadata-info">
              <Table
                className="project__metadata"
                columns={metadataColumns}
                dataSource={metadataData}
                pagination={false}
              />
            </div>
          </div>
          <div className="project__description">
            <div className="row project__description-header">
              <div className="col-xs-3">
                <b>Description</b>
              </div>
              <div className="col-xs-9" />
            </div>
            <div className="project__description-info">
              <Table
                className="project__metadata"
                columns={descriptionColumns}
                dataSource={descriptionData}
                pagination={false}
              />
            </div>
          </div>
          <div className="project__files">
            <div className="row project__files-header">
              <div className="col-xs-3">
                <b>Files</b>{" "}
              </div>
              <div className="col-xs-9" />
            </div>
            <div className="project__files-info">
              <Table
                className="project__metadata"
                columns={filesListColumns}
                dataSource={filesListData}
              />
            </div>
          </div>
        </div>
      </div>
    );
  }
}
