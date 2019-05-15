import React, { PureComponent, Fragment } from 'react';
import { Icon } from 'antd';
import cn from 'classnames';
import axios from 'axios';
import './Commit.sass';

const rowClass = type =>
  cn({
    commit__row: true,
    'commit__row--add': type === 'ADDITION',
    'commit__row--delete': type === 'DELETION'
  });

const typeDiff = type => {
  if (type === 'ADDITION') {
    return '+';
  }
  if (type === 'DELETION') {
    return '-';
  }
  return;
};
function DiffRowComponent({ data }) {
  return (
    <div className={rowClass(data.type)}>
      <div className="row commit__position">
        <div className="col-xs-6">{data.oldPosition}</div>
        <div className="col-xs-6">{data.newPosition}</div>
      </div>
      <div>
        <span className="commit__type">{typeDiff(data.type)}</span> {data.value}
      </div>
    </div>
  );
}

export default class Commit extends PureComponent {
  state = {
    changedFiles: []
  };

  async componentDidMount() {
    this.fetchCommitInfo();
  }

  fetchCommitInfo = async () => {
    const {
      match: {
        params: { projectId, commitId }
      }
    } = this.props;
    const { data } = await axios.get(`/api/projects/${projectId}/commits/${commitId}/changes`);
    this.setState({ changedFiles: data.changedFiles });
  };

  render() {
    const {
      match: {
        params: { commitId }
      }
    } = this.props;
    const { changedFiles } = this.state;
    return (
      <div className="container">
        <div>
          <h2>Commit #{commitId}</h2>
          {changedFiles.map(file => (
            <div className="commit__file">
              <div className="commit__file-info">
                TYPE: {file.statistics.type}
                <br />+ {file.statistics.addedLines} / - {file.statistics.deletedLines}
              </div>
              <div>
                {file.sections.map(section => (
                  <Fragment>
                    <div className="commit__section">
                      {section.items.map(item => (
                        <DiffRowComponent data={item} />
                      ))}
                    </div>
                    <Icon type="small-dash" />
                  </Fragment>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }
}
