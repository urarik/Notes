import ProjectList from './project_list';
import {Link} from 'react-router-dom';
import {useState} from 'react';
import Msg from '../MsgList';

export default function ProjectMain() {
  const [visible, setVisible] = useState(false);

  const onMsgClick = () => {
    setVisible(true);
  };

  return (
    <div className="project-main max-height">
      <header className="site-header">
        <h3 className="title">Notes!</h3>
      </header>
      <div className='content card-list'>
        <div className='header'>
          <i className='bx bx-bell msg' onClick={onMsgClick}></i>
          <Link color="" className='btn btn-outline-primary' to='/project/new'>
            새로운 프로젝트
          </Link>
        </div>
        <ProjectList />
      </div>
      {
        visible && <Msg setVisible={setVisible}/>
      }
    </div>
  );
}
