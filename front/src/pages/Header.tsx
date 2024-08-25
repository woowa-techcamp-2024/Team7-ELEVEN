import React from "react";

function Header() {

    // <nav className="mx-auto flex max-w-7xl items-center justify-between p-6 lg:px-8 sticky top-0" aria-label="Global">

    return (
        <div>
            <header className="bg-white sticky top-0">
                <nav className="mx-auto flex max-w-7xl items-center justify-between p-6 lg:px-8" aria-label="Global">
                    <div className="flex lg:flex-1">
                        <a href="#" className="-m-1.5 p-1.5">
                            <img className="h-8 w-auto"
                                 src="https://tailwindui.com/img/logos/mark.svg?color=indigo&shade=600" alt=""/>
                        </a>
                    </div>
                    <div className="flex lg:flex-1">
                        <h1>럭키비키 경매</h1>
                    </div>
                </nav>
            </header>
        </div>
    );
}

export default Header
