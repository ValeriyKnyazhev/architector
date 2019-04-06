import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import _isEmpty from 'lodash/isEmpty';
import { Button, Icon, Input, message } from 'antd';
import './Project.sass';

export default class Project extends Component {
  state = {
    project: {
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
  };

  async fetchProject() {
    const {
      match: {
        params: {projectId}
      }
    } = this.props;
    console.log(this.props)
    const { data } = await axios.get(`/api/projects/${projectId}`);
    this.setState({project: data});
    console.log(this.state)
  };

  render() {
    const {
      match: {
        params: {projectId}
      }
    } = this.props;
    const {
      project
    } = this.state;
    // TODO remove duplication
    project.files = project.files.concat(project.files).sort()
    return (
      <div className="container">
        <div>
          <h1>Project № {projectId}</h1>
        </div>
        <div>
          <div className="row projects__project-date">
            <div className="col-xs-3">Created</div>
            <div className="col-xs-9">{project.createdDate}</div>
          </div>
          <div className="row projects__project-date">
            <div className="col-xs-3">Updated</div>
            <div className="col-xs-9">{project.updatedDate}</div>
          </div>
          <div className="row projects__project-schema">
            <div className="col-xs-3">Schema</div>
            <div className="col-xs-9">{project.schema}</div>
          </div>
          <div className="project__metadata">
            <div className="row project__metadata-header">
              <div className="col-xs-3"><b>Metadata</b> </div>
              <div className="col-xs-9"></div>
            </div>
            <div className="project__metadata-info">
              <div className="row project__metadata-info-name">
                <div className="col-xs-3">Name</div>
                <div className="col-xs-9">{project.metadata.name}</div>
              </div>
              <div className="row project__metadata-info-authors">
                <div className="col-xs-3">Authors</div>
                <div className="col-xs-9">
                  {
                    project.metadata.authors.filter(a => a).length > 0
                      ? project.metadata.authors.reduce((a1, a2) => a1 + ", " + a2)
                      : 'N/A'
                  }
                </div>
              </div>
            </div>
            <div className="row project__metadata-info-organizations">
              <div className="col-xs-3">Organizations</div>
              <div className="col-xs-9">
                {
                  project.metadata.organizations.filter(o => o).length > 0
                    ? project.metadata.organizations.reduce((o1, o2) => o1 + ", " + o2)
                    : 'N/A'
                }
              </div>
            </div>
            <div className="row project__metadata-info-originating-system">
              <div className="col-xs-3">Originating system</div>
              <div className="col-xs-9">{project.metadata.originatingSystem}</div>
            </div>
            <div className="row project__metadata-info-preprocessor-version">
              <div className="col-xs-3">Preprocessor version</div>
              <div className="col-xs-9">{project.metadata.preprocessorVersion}</div>
            </div>

          </div>
          <div className="project__description">
            <div className="row project__description-header">
              <div className="col-xs-3"><b>Description</b> </div>
              <div className="col-xs-9"></div>
            </div>
            <div className="project__description-info">
              <div className="row project__description-info-descriptions">
                <div className="col-xs-3">Descriptions</div>
                <div className="col-xs-9">
                  {project.description.descriptions.map(description => <div>{description}</div>)}
                </div>
              </div>
              <div className="row project__description-info-implementation-level">
                <div className="col-xs-3">Implementation level</div>
                <div className="col-xs-9">{project.description.implementationLevel}</div>
              </div>
            </div>
          </div>
          <div className="project__files">
            <div className="row project__files-header">
              <div className="col-xs-3"><b>Files</b> </div>
              <div className="col-xs-9"></div>
            </div>
            <div className="row project__files-info">
              <div className="col-xs-3"></div>
              <div className="col-xs-9 project__files-list">
                <div>
                  {
                    project.files.map((file, index) =>
                      <div className="project__files_file">
                        <div><b>{index+1}</b>     {file.fileId}</div>
                        <div>     {file.createdDate}</div>
                        <div>     {file.updatedDate}</div>
                      </div>
                    )
                  }
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }
}