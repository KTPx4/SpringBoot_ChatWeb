import { parseISO } from 'date-fns';
import { toZonedTime, format } from 'date-fns-tz';

const useHCMTime = () => {
    const convertToHCMTime = (dateString) => {
        const utcDate = parseISO(dateString);
        const timeZone = 'Asia/Ho_Chi_Minh';
        const hcmDate = toZonedTime(utcDate, timeZone);
        return format(hcmDate, 'yyyy-MM-dd HH:mm', { timeZone });
    };

    return { convertToHCMTime };
};

export default useHCMTime;
