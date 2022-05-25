
export default function({fragment, setFragment, lifeLines}) {
    const style = {
        position: 'absolute',
        top: fragment.relTop,
        left: fragment.relLeft,
        width: fragment.relW,
        height: fragment.relH,
        cursor: 'pointer',
        zIndex: 9999,
        border: '1px solid black'
    };
    // 모든 lifeline의 fontsize는 같으므로 임의로 선택
    const fontSize = lifeLines[fragment.fromId].plane.fontSize;
    
    const typeStyle = {
        position: 'relative',
        left: '0',
        top: '0',
        border: '1px black solid',
        width: fragment.relW / 8,
        fontSize: fontSize/1.3,
        backgroundColor: 'var(--primary-color-light)'
    }

    const contentStyle = {
        position: 'relative',
        top: '0',
        left: '0',
        fontSize: fontSize/1.4,
        width: fragment.relW /4
    }

    return (<div style={style} className="fragment">
        <div className="header">
            <div style={typeStyle} className="type">
                <div>{fragment.type}</div>
            </div>
            <div style={contentStyle} className="content">[{fragment.content}]</div>
        </div>
    </div>)
}
