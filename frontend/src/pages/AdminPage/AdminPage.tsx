import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Header from '@/components/common/Header/Header';
import Spinner from '@/components/common/Spinner/Spinner';
import { STORAGE_KEYS } from '@/constants/storageKeys';
import { useGetClubDetail } from '@/hooks/Queries/useClub';
import PersonalInfoConsentModal from '@/pages/AdminPage/components/PersonalInfoConsentModal/PersonalInfoConsentModal';
import SideBar from '@/pages/AdminPage/components/SideBar/SideBar';
import { useAdminClubId } from '@/store/useAdminClubStore';
import * as Styled from './AdminPage.styles';

const AdminPage = () => {
  const { clubId } = useAdminClubId();
  const [hasConsented, setHasConsented] = useState(
    () =>
      localStorage.getItem(STORAGE_KEYS.HAS_CONSENTED_PERSONAL_INFO) === 'true',
  );
  const { data: clubDetail, error, isLoading } = useGetClubDetail(clubId || '');

  if (isLoading || (!clubDetail && !error)) {
    return <Spinner />;
  }

  if (error || !clubDetail) {
    return <p>Error: {error?.message || '동아리 정보를 불러올 수 없습니다.'}</p>;
  }

  return (
    <>
      <Header hideOn={['mobile', 'tablet']} />
      {!hasConsented && (
        <PersonalInfoConsentModal
          clubName={clubDetail.name}
          onConsent={() => setHasConsented(true)}
        />
      )}
      <Styled.Background>
        <Styled.Layout>
          <SideBar />
          <Styled.MainContent>
            <Outlet context={clubDetail} />
          </Styled.MainContent>
        </Styled.Layout>
      </Styled.Background>
    </>
  );
};

export default AdminPage;
