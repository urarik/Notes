import chevron from '../../../images/chevron.svg';
import arrow from '../../../images/arrow.svg';
import squre from '../../../images/square.svg';


export default function({type, point, degree, width}) {
    let src;
    switch(type) {
        case 'Association':
        case 'Dependency':
            src = chevron;
            break;
        case 'Realization':
        case 'Generalization':
            src = arrow;
            break;
        case 'Composition':
            src = squre;
            break;
        default:
            src = '';
    }

    const style = {
        position: 'absolute',
        right: '0',
        width: width,
        height: width
    };

    const visible = src !== '';
    return (
        <>
        {
            visible &&
            <img src={src} style={style}></img>
        }
        </>
    )
}