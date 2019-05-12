import React, { PureComponent } from 'react';
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
        <div className="col-xs-12">{data.oldPosition}</div>
        <div className="col-xs-12">{data.newPosition}</div>
      </div>
      <div>
        {typeDiff(data.type)} {data.value}
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
              {file.sections.map(section => (
                <div>
                  {section.items.map(item => (
                    <DiffRowComponent data={item} />
                  ))}
                </div>
              ))}
            </div>
          ))}
        </div>
      </div>
    );
  }
}
