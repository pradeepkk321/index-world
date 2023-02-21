import { Link } from 'react-router-dom';
import './Header.scss'

export default function Header() {

  return (
    <div className="Header">
      <h1>Index World</h1>

      <nav>
        <ul>
          <li><Link className='link' to="/">Home</Link></li>
          <li><Link className='link' to="/collections">Collections</Link></li>
        </ul>
      </nav>

{/* <ul>
  <li><a class="active" href="#home">Home</a></li>
  <li><a href="#news">News</a></li>
  <li><a href="#contact">Contact</a></li>
  <li><a href="#about">About</a></li>
</ul> */}

    </div>
  );
}