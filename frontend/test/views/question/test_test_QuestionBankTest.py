import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/questionBank"
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
    return browser, context, page

@pytest.mark.parametrize("credentials, is_teacher, is_admin", [
    (TEACHER_CREDENTIALS, True, False),
    (STUDENT_CREDENTIALS, False, False),
    (ADMIN_CREDENTIALS, False, True)
])
def test_question_bank_render(playwright, credentials, is_teacher, is_admin):
    browser, context, page = login_and_get_page(playwright, credentials, BASE_URL)
    if is_admin:
        # 管理员应被重定向到/manage/teachers
        expect(page).to_have_url(re.compile(r"http://localhost:3000/manage/teachers"))
        context.close()
        browser.close()
        return
    expect(page).to_have_url(re.compile(r"http://localhost:3000/questionBank"))
    expect(page.locator("h2")).to_have_text("题库中心")
    expect(page.locator(".resource-filters")).to_be_visible()
    # 检查题目卡片渲染
    if page.locator(".question-card").count() > 0:
        expect(page.locator(".question-card").first).to_be_visible()
        # 查看详情弹窗
        page.locator(".question-card .btn-action.preview").first.click()
        expect(page.locator(".modal-header h3")).to_have_text("题目详情")
        expect(page.locator(".modal-body")).to_be_visible()
        page.locator(".modal-close").click()
        expect(page.locator(".modal-mask")).not_to_be_visible(timeout=3000)
    else:
        assert page.locator(".empty-state").is_visible()
    # 教师身份可见新建/生成按钮
    if is_teacher:
        expect(page.locator("button.btn-primary:has-text('新建题目')")).to_be_visible()
        expect(page.locator("button.btn-secondary:has-text('生成题目')")).to_be_visible()
    else:
        expect(page.locator("button.btn-primary:has-text('新建题目')")).not_to_be_visible()
        expect(page.locator("button.btn-secondary:has-text('生成题目')")).not_to_be_visible()
    context.close()
    browser.close()
