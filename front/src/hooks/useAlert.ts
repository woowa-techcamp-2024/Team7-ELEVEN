import {useAlertStore} from '../store/AlertStore';

const useAlert = () => {
    const {setShow, setMessage} = useAlertStore();

    const showAlert = (message: string) => {
        setShow(true);
        setMessage(message);
    };

    return {showAlert};
};

export default useAlert;
