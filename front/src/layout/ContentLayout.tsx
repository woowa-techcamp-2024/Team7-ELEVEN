import React, {ReactNode} from 'react';

interface Props {
    children: ReactNode;
}

const CustomComponent: React.FC<Props> = ({children}) => {
    return (
        <div className="container mx-auto p-4 pb-[64px] mb-[64px]">
            {children}
        </div>
    );
};

export default CustomComponent;
