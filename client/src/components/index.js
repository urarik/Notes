import {Link} from 'react-router-dom';


function Index() {
    return (
        <div id="cover" className="text-center max-height">
        <div className="cover-container d-flex h-100 p-3 mx-auto flex-column">
          <header className="masthead mb-auto">
            <div className="inner">
              <h3 className="masthead-brand">Cover</h3>
              <nav className="nav nav-masthead justify-content-center">
                <Link className="cover-link nav-link active" to="/signin">Sign In</Link>
                <Link className="cover-link nav-link active" to="/signup">Sign up</Link>
              </nav>
            </div>
          </header>
    
          <main role="main" className="inner cover">
            <h1 className="cover-heading">Notes!</h1>
            <p className="lead">Make your own notes including powerful functionality about dynamic source code analysis.</p>
            <p className="lead">
              <a href="#" className="btn btn-lg cover-btn-secondary">Learn more</a>
            </p>
          </main>
    
          <footer className="mastfoot mt-auto">
            <div className="inner">
              <p>Cover template for <a href="https://getbootstrap.com/">Bootstrap</a>, by <a href="https://twitter.com/mdo">@mdo</a>.</p>
            </div>
          </footer>
        </div>
        </div>
    );
}
export default Index;