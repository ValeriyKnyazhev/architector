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

function DiffMetadataValue({ descr, data }) {
  return (
    <>
      <div style={{ textAlign: 'left', margin: '8px 0 6px 0' }} className="around">
        {descr}:
      </div>
      <div className="d-flex around">
        <div className="commit__row--delete around">{data.oldValue}</div>
        <div className="commit__row--add around">{data.newValue}</div>
      </div>
    </>
  );
}

function DiffMetadataValues({ descr, data }) {
  return (
    <>
      <div style={{ textAlign: 'left', margin: '8px 0 6px 0' }} className="around">
        {descr}:
      </div>
      <div className="d-flex around">
        <div className="commit__row--delete around">
          {data.oldValue &&
            data.oldValue.map((value, index) => (
              <div className="start-xs" key={index}>
                {value}
              </div>
            ))}
        </div>
        <div className="commit__row--add around">
          {data.newValue &&
            data.newValue.map((value, index) => (
              <div className="start-xs" key={index}>
                {value}
              </div>
            ))}
        </div>
      </div>
    </>
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
    const { changedFiles, name = '', description = '' } = this.state;
    return (
      <div className="container">
        <div>
          <h2>Commit #{commitId}</h2>
          <div style={{ textAlign: 'left' }}>Project Name: {name}</div>
          <div style={{ textAlign: 'left' }}>Project Description: {description}</div>
          {changedFiles.map((file, index) => (
            <Fragment key={index}>
              <p style={{ textAlign: 'left' }}>File name: {file.name}</p>
              <div className="commit__file">
                <div className="commit__file-info">
                  TYPE: {file.statistics.type}
                  <br />
                  {file.sections.length > 0 && (
                    <div>
                      {file.statistics.addedLines} / - {file.statistics.deletedLines}
                    </div>
                  )}
                </div>
                {file.metadata.name !== null && (
                  <DiffMetadataValue descr="Metadata Name" data={file.metadata.name} />
                )}
                {file.metadata.authors !== null && (
                  <DiffMetadataValues descr="Metadata Authors" data={file.metadata.authors} />
                )}
                {file.metadata.organizations !== null && (
                  <DiffMetadataValues
                    descr="Metadata Organizations"
                    data={file.metadata.organizations}
                  />
                )}
                {file.metadata.preprocessorVersion !== null && (
                  <DiffMetadataValue
                    descr="Metadata Preprocessor version"
                    data={file.metadata.preprocessorVersion}
                  />
                )}
                {file.metadata.originatingSystem !== null && (
                  <DiffMetadataValue
                    descr="Metadata Originating System"
                    data={file.metadata.originatingSystem}
                  />
                )}
                {file.description.descriptions !== null && (
                  <DiffMetadataValues
                    descr="Description tags"
                    data={file.description.descriptions}
                  />
                )}
                {file.description.implementationLevel !== null && (
                  <DiffMetadataValue
                    descr="Description implemenation level"
                    data={file.description.implementationLevel}
                  />
                )}
                <div style={{ marginTop: '8px' }}>
                  <div style={{ textAlign: 'left' }}>Content: </div>
                  {file.sections.map((section, i, { length }) => (
                    <Fragment key={i}>
                      <div className="commit__section">
                        {section.items.map((item, index) => (
                          <DiffRowComponent data={item} key={index} />
                        ))}
                      </div>
                      {i + 1 !== length && <Icon type="small-dash" />}
                    </Fragment>
                  ))}
                </div>
              </div>
            </Fragment>
          ))}
        </div>
      </div>
    );
  }
}
