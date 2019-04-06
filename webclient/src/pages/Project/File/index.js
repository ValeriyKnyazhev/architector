import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import _isEmpty from 'lodash/isEmpty';
import { Button, Icon, Layout, message } from 'antd';
import './File.sass';

const { Header, Content, Footer } = Layout;

export default class File extends Component {
  state = {
    file: {
      content: {
        items: []
      }
    }
  };

  async componentDidMount() {
    this.fetchFileInfo.call(this);
  };

  async fetchFileInfo() {
    const {
      location: {
        state: { projectId, fileId }
      }
    } = this.props;
    const { data } = await axios.get(`/api/projects/${projectId}/files/${fileId}`);
    this.setState({file: data});
  };

  render() {
    const {
      location: {
        state: { projectId, fileId }
      }
    } = this.props;
    const { file } = this.state;
    return (
      <div className="container">
        <div>
          <h2>File № {fileId}</h2>
        </div>
        <div>
          <div>
            <div className="row files__file-date">
              <div className="col-xs-3">Created</div>
              <div className="col-xs-9">{file.createdDate}</div>
            </div>
            <div className="row files__file-date">
              <div className="col-xs-3">Updated</div>
              <div className="col-xs-9">{file.updatedDate}</div>
            </div>
            <div className="files__file-content">
              <div className="row files__file-content-header">
                <div className="col-xs-3"><b>Content</b> </div>
                <div className="col-xs-9"></div>
              </div>
              <Layout className="files__file-content-info" style={{ marginLeft: 100 }}>
                <Header style={{ background: '#fff', alpha: 0.2, padding: 0 }} />
                <Content style={{ margin: '12px 8px 0', overflow: 'initial' }}>
                  {file.content.items.map((item, index) =>
                    <div style={{ padding: 6, background: '#fff', textAlign: 'left' }}>
                      {item}
                    </div>
                  )}
                </Content>
                <Footer style={{ textAlign: 'center' }} />
              </Layout>
            </div>
          </div>
        </div>
      </div>
    );
  }
}