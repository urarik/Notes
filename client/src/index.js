import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { createStore, applyMiddleware } from 'redux';
import { BrowserRouter, Route, Routes} from 'react-router-dom';
import persistStore from 'redux-persist/es/persistStore';

import reducers from './reducers';

import './style/style.css';
import 'bootstrap/dist/css/bootstrap.min.css';


import Index from './components/index';
import ProjectMain from './components/before_project/ProjectMain';
import ProjectIndex from './container/project_index';
import SignIn from './components/SignIn';
import SignUp from './components/SignUp';
import ProjectNew from './components/before_project/ProjectNew';
import Msg from './components/MsgList'
import { PersistGate } from 'redux-persist/integration/react';

const createStoreWithMiddleware = applyMiddleware()(createStore);
const store = createStoreWithMiddleware(reducers);
const persistor = persistStore(store);

ReactDOM.render(
  <Provider store={store}>
    <PersistGate loading={null} persistor={persistor}>
      <BrowserRouter>
        <Routes>
          <Route path='/' element={<Index />} />
          <Route path='/projects' element={<ProjectMain />} />
          <Route path='/project/new' element={<ProjectNew />} />
          <Route path='/project/:id/*' element={<ProjectIndex />} />
          <Route path='/signin' element={<SignIn />} />
          <Route path='/signup' element={<SignUp />} />
          <Route path='/msg' element={<Msg />} />
        </Routes>
      </BrowserRouter>
    </PersistGate>
  </Provider>,
  document.getElementById('root')
);

// https://blog.bitsrc.io/react-hooks-beyond-usestate-useeffect-f54dd6df6d1d
// https://blog.bitsrc.io/writing-your-own-custom-hooks-4fbcf77e112e