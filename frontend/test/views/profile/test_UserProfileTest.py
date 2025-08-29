import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/profile"
LOGIN_URL = "http://localhost:3000/login"

TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}
ADMIN_CREDENTIALS = {"userId": "A001", "password": "123456"}

def login_and_get_page(playwright, credentials: dict, target_url: str):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    page.goto(target_url)
    expect(page).to_have_url(re.compile(r"http://localhost:3000/profile"))
    return browser, context, page

@pytest.mark.parametrize("credentials", [
    TEACHER_CREDENTIALS,
    STUDENT_CREDENTIALS,
    ADMIN_CREDENTIALS
])
def test_user_profile_render(playwright, credentials):
    browser, context, page = login_and_get_page(playwright, credentials, BASE_URL)
    expect(page.locator("h1.page-title")).to_have_text("维护个人信息")
    expect(page.locator(".profile-card")).to_be_visible()
    expect(page.locator(".user-name")).to_be_visible()
    expect(page.locator(".user-id")).to_be_visible()
    expect(page.locator(".user-role")).to_be_visible()
    expect(page.locator("input#username")).to_be_visible()
    expect(page.locator("input#email")).to_be_visible()
    expect(page.locator("input#userId")).to_be_visible()
    # 切换到修改密码标签
    page.locator("button:has-text('修改密码')").click()
    expect(page.locator("form")).to_be_visible()
    expect(page.locator("input#currentPassword")).to_be_visible()
    expect(page.locator("input#newPassword")).to_be_visible()
    expect(page.locator("input#confirmPassword")).to_be_visible()
    context.close()
    browser.close()

